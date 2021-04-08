package com.jarvis.dts.strategy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.jarvis.dts.canal.constant.RowEventTypeEnum;

/**
 * T为对应的实体类
 *
 * @Author: Hu Xin
 * @Date: 2021/4/2 10:27
 * @Desc:
 **/
public interface DataTransmitStrategy<T> {

    /**
     * 获取对应数据库实体entity类
     *
     * @return
     */
    default Class<T> getEntityType() {
        ParameterizedType parameterizedType = (ParameterizedType)this.getClass().getGenericSuperclass();
        Type[] genericTypes = parameterizedType.getActualTypeArguments();
        Class<T> retClass = (Class<T>)genericTypes[0];
        return retClass;
    }

    /**
     * 修改操作对应的处理
     *
     * @param row
     */
    void update(T row);

    /**
     * 删除操作对应的处理
     *
     * @param row
     */
    void delete(T row);

    /**
     * 插入操作对应的处理
     *
     * @param row
     */
    void insert(T row);

    /**
     * 根据对应的类型分发到不同的方法上
     *
     * @param eventType
     *            操作类型 CRUD
     * @param instance
     *            行对象
     */
    default void dispatchToMethod(RowEventTypeEnum eventType, Object instance) {

        if (eventType == RowEventTypeEnum.INSERT) {
            this.insert((T)instance);
        } else if (eventType == RowEventTypeEnum.UPDATE) {
            this.update((T)instance);
        } else if (eventType == RowEventTypeEnum.DELETE) {
            this.delete((T)instance);
        }
    }
}
