package com.jarvis.dts.canal.task;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.base.CaseFormat;
import com.jarvis.dts.annotation.Strategy;
import com.jarvis.dts.canal.CanalColumn;
import com.jarvis.dts.canal.CanalRowEvent;
import com.jarvis.dts.canal.CanalServerConfig;
import com.jarvis.dts.canal.constant.RowEventTypeEnum;
import com.jarvis.dts.factory.StrategyFactory;
import com.jarvis.dts.strategy.DataTransmitStrategy;
import com.jarvis.dts.util.Reflect;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/1 15:28
 * @Desc:
 **/
@Slf4j
public abstract class BaseDataTransmitTask extends AbstractDataTransmitTask {

    private StrategyFactory strategyFactory;
    /**
     * true:返回所有字段。【默认】 false:返回的字段是"updated" or "pk"
     */
    private boolean returnAllColumnsIgnoreModified = true;

    /**
     * <EntityClass,<xxx_xx,Method>>
     */
    private static ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> classColumnMethodCache =
        new ConcurrentHashMap<>(3);

    /**
     * <EntityClass,<xxx_xx,Field>>
     */
    private static ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Field>> classColumnFieldCache =
        new ConcurrentHashMap<>(2);

    private static final String formatterMill = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final String formatter = "yyyy-MM-dd HH:mm:ss";

    private static final String dateFormatter = "yyyy-MM-dd";

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern(formatterMill);

    @Override
    protected void doProcess(List<CanalRowEvent> rows) {
        // 这里应该不能用多线程，因为binlog日志是有顺序性的
        for (CanalRowEvent row : rows) {
            String schema = row.getSchemaName();
            String table = row.getTableName();
            String customizedSchema = customizeSchemaConverted(schema);
            String customizedTable = customizeSchemaConverted(table);
            /**
             * 1.创建对应策略
             */
            List<DataTransmitStrategy> strategyList = createStrategy(customizedSchema, customizedTable);
            if (CollectionUtils.isEmpty(strategyList)) {
                log.debug("找不到schema:{},table:{},converted-schema:{},converted-table:{},对应的处理策略，跳过此行的处理", schema, table,
                        customizedSchema, customizedTable);
                continue;
            }
            // 操作类型
            RowEventTypeEnum eventType = row.getEventType();

            /**
             * 再做一次清洗成entity 2.根据策略上指定的entity,将row生成对应instance。 如果使用的是默认的策略
             * {@link com.jarvis.dts.strategy.DefaultDataTransmitStrategy}，则只是打印出CanalRowEvent信息 strategyList中 对同一个表
             * 对应的实体必须是同一个
             */
            checkStrategyForEntityIsSame(strategyList);


            /**
             * 再做一次清洗成entity 2.根据策略上指定的entity,将row生成对应instance。 如果使用的是默认的策略
             * {@link com.jarvis.dts.strategy.DefaultDataTransmitStrategy}，则只是打印出CanalRowEvent信息
             */
            Class<?> clazz = strategyList.get(0).getEntityType();

            if (null == clazz) {
                log.error("strategy:{},schema:{},table:{},找不到对应的实体类，跳过此行:{}的处理", strategyList.get(0).getClass(), schema,
                        table, row);
                continue;
            }

            if (clazz != CanalRowEvent.class) {
                Object instance = createInstance(clazz, row, strategyList.get(0));
                /**
                 * 3.根据操作类型，调用不同的处理方法
                 */
                for (DataTransmitStrategy strategy : strategyList) {
                    try {
                        strategy.dispatchToMethod(eventType, instance);
                    } catch (Exception e) {
                        log.error("策略:{},数据:{},执行报错...", strategy, instance, e);
                    }
                }
            } else {
                for (DataTransmitStrategy strategy : strategyList) {
                    try {
                        strategy.dispatchToMethod(eventType, row);
                    } catch (Exception e) {
                        log.error("策略:{},数据:{},执行报错...", strategy, row, e);
                    }
                }
            }

        }

    }

    /**
     * 根据canal行数据信息，entity类。生成对应的实例
     *
     * @param clazz
     *            实体
     * @param row
     * @return 字段没有变更 或 字段找不到对应的 set方法则跳过该字段的处理
     */
    protected Object createInstance(Class<?> clazz, CanalRowEvent row, DataTransmitStrategy strategy) {
        // 利用驼峰字段的set方法进行赋值
        try {
            List<CanalColumn> canalColumnList = row.getColumnList();
            Object instance = clazz.getConstructor().newInstance();
            log.debug("createInstance,实例化结果instance :{},class:{}", instance, instance.getClass().getSimpleName());
            for (CanalColumn column : canalColumnList) {
                if (!this.returnAllColumnsIgnoreModified) {
                    // 没有更新的 and 非主键字段，跳过
                    if (!column.isUpdated() && !isPkColumn(strategy, column.getColumnName())) {
                        log.debug("字段：{},没有变更,原来值:{}", column.getColumnName(), column.getColumnValue());
                        continue;
                    }
                }

                String columnValue = column.getColumnValue();
                // convert value
                Method method = getMethodOrRefreshCache(clazz, column.getColumnName(), canalColumnList);
                if (null != method) {
                    // 默认set方法只允许有一个形参
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Object fieldValue = convertString2JavaObject(columnValue, parameterTypes[0]);
                    if (null == fieldValue) {
                        continue;
                    }
                    method.setAccessible(true);
                    method.invoke(instance, fieldValue);
                }

            }
            return instance;
        } catch (Exception e) {
            log.error("BaseDataTransmitTask#createInstance,class:" + clazz + "，实例化出错...", e);
            return null;
        }
    }

    /**
     * 是否为业务规定的主键字段
     *
     * @param strategy
     * @param columnName
     * @return
     */
    private boolean isPkColumn(DataTransmitStrategy strategy, String columnName) {
        Strategy annotation = strategy.getClass().getDeclaredAnnotation(Strategy.class);
        if (null != annotation) {
            String[] pkArray = annotation.pk();
            if (null != pkArray && pkArray.length > 0) {
                for (String pk : pkArray) {
                    if (columnName.equalsIgnoreCase(pk)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 生成对应策略
     *
     * @param schema
     * @param table
     * @return
     */
    protected List<DataTransmitStrategy> createStrategy(String schema, String table) {
        if (strategyFactory != null) {
            List<DataTransmitStrategy> strategys =
                    (List<DataTransmitStrategy>)strategyFactory.createStrategy(schema, table);
            return strategys;
        } else {
            log.debug("schema:{},table:{},找不到配置的策略生成工厂...,请配置对应的工厂。。。", schema, table);
        }
        return null;
    }

    // /**
    // * 通过mybatisPlus 的表名，字段名注解。直接对字段进行赋值。
    // *
    // * @param clazz
    // * @param row
    // * @return 精确度较高，但是效率较低。
    // * TODO 可优化为用内存缓存存储对应关系
    // */
    // private Object createInstanceByFieldAnnotation(Class clazz, CanalRowEvent row) {
    // try {
    // Object instance = clazz.getConstructor().newInstance();
    // String tableName = row.getTableName();
    // TableName annotation = (TableName) clazz.getAnnotation(TableName.class);
    // if (clazz.isAnnotationPresent(TableName.class)) {
    // if (annotation.value().equals(tableName)) {
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // Field[] fields = clazz.getDeclaredFields();
    // for (CanalColumn column : row.getColumnList()) {
    // //只关注变化过的字段
    // if (!column.isUpdated()) {
    // log.info("字段：{},没有变更,原来值:{}", column.getColumnName(), column.getColumnValue());
    // continue;
    // }
    // String columnName = column.getColumnName();
    // String columnValue = column.getColumnValue();
    // for (Field field : fields) {
    // TableField fieldAnnotation = field.getAnnotation(TableField.class);
    // if (null != fieldAnnotation && null != fieldAnnotation.value() && fieldAnnotation.value().equals(columnName)) {
    // field.setAccessible(true);
    // field.set(instance, convertString2JavaObject(columnValue, field.getType()));
    // }
    // }
    // }
    // return instance;
    // } else {
    // log.error("class:{} 上@tableName注解:{},与canal数据中tableName:{}不对应", clazz, annotation.value(), tableName);
    // return null;
    // }
    //
    // } else {
    // log.error("class:{} 上没有找到@tableName的注解", clazz);
    // return null;
    // }
    // } catch (Exception e) {
    // log.error("根据class:" + clazz + "，实例化出错...", e);
    // return null;
    // }
    //
    // }

    protected void setStrategyFactory(@NotNull StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    protected void initTransmitTask(@NotNull StrategyFactory factory, @NotNull CanalServerConfig clientConfig) {
        setStrategyFactory(factory);
        setCanalServerConfig(clientConfig);
    }

    /**
     * 根据entity字段类型，转化字段值
     *
     * @param sourceValue
     * @param targetType
     * @return
     */
    private Object convertString2JavaObject(String sourceValue, Class<?> targetType) {
        if (StringUtils.isEmpty(sourceValue)) {
            return null;
        }
        try {
            if (targetType == String.class) {
                return sourceValue;
            } else if (targetType == Integer.class) {
                return Integer.valueOf(sourceValue);
            } else if (targetType == Long.class) {
                return Long.valueOf(sourceValue);
            } else if (targetType == Double.class) {
                return Double.valueOf(sourceValue);
            } else if (targetType == Date.class) {
                sourceValue = getStandardDate(sourceValue);
                LocalDateTime ldt = LocalDateTime.parse(sourceValue, df);
                Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                return date;
            } else if (targetType == LocalDateTime.class) {
                sourceValue = getStandardDate(sourceValue);
                return LocalDateTime.parse(sourceValue, df);
            }
        } catch (Exception e) {
            log.error("convertString2JavaObject 格式转化错误,sourceValue:{},targetType:{}", sourceValue,
                    targetType.getSimpleName());
        }
        return null;
    }

    /**
     * 根据数据库字段名字，在指定的entity类中，查找对应的set方法。
     *
     * @param cls
     * @param columnName
     * @param columnList
     * @return 若找不到对应entity class的信息则扫描类信息，加载到缓存中
     */
    private Method getMethodOrRefreshCache(Class<?> cls, String columnName, List<CanalColumn> columnList) {
        ConcurrentHashMap<String, Method> stringMethodConcurrentHashMap = classColumnMethodCache.get(cls);
        if (CollectionUtils.isEmpty(stringMethodConcurrentHashMap)) {
            stringMethodConcurrentHashMap = new ConcurrentHashMap<>();
            // 找不到类对应的信息
            List<Method> setMethods = Reflect.getMethodByName(cls, Reflect.MethodHeadName.SET, null);
            for (CanalColumn column : columnList) {
                String name = underlineToCamel(column.getColumnName());
                String setMethodName =
                    Reflect.MethodHeadName.SET.name + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method methodFound =
                    setMethods.stream().filter(m -> m.getName().equals(setMethodName)).findFirst().orElse(null);
                if (null != methodFound) {
                    stringMethodConcurrentHashMap.put(column.getColumnName(), methodFound);
                }

            }
            classColumnMethodCache.put(cls, stringMethodConcurrentHashMap);
        }
        return stringMethodConcurrentHashMap.get(columnName);
    }

    protected void setReturnAllColumnsIgnoreModified(boolean returnAllColumnsIgnoreModified) {
        this.returnAllColumnsIgnoreModified = returnAllColumnsIgnoreModified;
    }

    /**
     * 下划线转驼峰
     *
     * @param columnName
     * @return
     */
    private String underlineToCamel(String columnName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
    }

    public void init(CanalServerConfig config, StrategyFactory factory) {
        setCanalServerConfig(config);
        setStrategyFactory(factory);
    }

    private void checkStrategyForEntityIsSame(List<DataTransmitStrategy> strategyList) {
        Class<?> clazz = strategyList.get(0).getEntityType();
        for (DataTransmitStrategy strategy : strategyList) {
            Class entityType = strategy.getEntityType();
            if (clazz != entityType) {
                log.error("针对同一张表的处理策略中，对应了不同的实体类..", clazz, entityType);
                throw new RuntimeException("针对同一张表的处理策略中，对应了不同的实体类...");
            } else {
                clazz = entityType;
            }

        }
    }

    /**
     * 时间格式标准化
     * @param sourceValue
     * @return
     */
    private String getStandardDate(String sourceValue) {
        String targetValue = sourceValue;
        if (sourceValue.length() >= formatterMill.length()) {
            targetValue = sourceValue.substring(0, formatterMill.length());
        } else if (sourceValue.length() >= formatter.length()) {
            targetValue = sourceValue.substring(0, formatter.length()) + ".000";
        } else if (sourceValue.length() >= dateFormatter.length()) {
            targetValue = sourceValue.substring(0, dateFormatter.length()) + " 00:00:00.000";
        }
        return targetValue;
    }


}
