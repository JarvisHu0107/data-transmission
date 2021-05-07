package com.jarvis.dts.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.jarvis.dts.annotation.Strategy;
import com.jarvis.dts.strategy.BaseDataTransmitStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据传输策略工厂
 *
 * @Author: Hu Xin
 * @Date: 2021/4/1 15:34
 * @Desc:
 **/
@Slf4j
@Component("dtsFactory")
public class DataTransmitStrategyFactory implements InitializingBean, StrategyFactory<BaseDataTransmitStrategy> {

    @Autowired
    private List<BaseDataTransmitStrategy> strategyList;

    private ConcurrentHashMap<String, List<BaseDataTransmitStrategy>> strategyMap = new ConcurrentHashMap<>(3);

    @Override
    public void afterPropertiesSet() {
        // 找到所有TableSyncStrategy的子类，将映射关系放入Map中
        strategyList.stream().forEach(strategy -> {
            Class clz = strategy.getClass();
            // 获取指定注解
            if (clz.isAnnotationPresent(Strategy.class)) {
                Strategy annotation = (Strategy)clz.getAnnotation(Strategy.class);
                String schema = annotation.schema();
                String table = annotation.table();
                if (org.apache.commons.lang.StringUtils.isNotEmpty(schema) && StringUtils.isNotEmpty(table)) {
                    List<BaseDataTransmitStrategy> strategyList = strategyMap.get(getStrategyKey(schema, table));
                    if (null == strategyList) {
                        strategyList = new ArrayList<>();
                    }
                    strategyList.add(strategy);
                    strategyMap.put(getStrategyKey(schema, table), strategyList);
                }
            }
        });
    }

    @Override
    public List<BaseDataTransmitStrategy> createStrategy(String schema, String table) {

        List<BaseDataTransmitStrategy> strategys = strategyMap.get(getStrategyKey(schema, table));
        try {
            if (!CollectionUtils.isEmpty(strategys)) {
                return strategys;
            }
        } catch (Exception e) {
            log.error("schema:{},table:{},获取对应入库策略失败", schema, table);
            return null;
        }
        return null;
    }

    private static String getStrategyKey(String schema, String table) {
        return schema + ":" + table;
    }

}
