package com.jarvis.dts.factory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jarvis.dts.annotation.Strategy;
import com.jarvis.dts.strategy.BaseDataTransmitToElasticStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 工厂 - 数据传输到ES的策略
 *
 * @Author: Hu Xin
 * @Date: 2021/4/1 15:34
 * @Desc:
 **/
@Slf4j
@Component("es")
public class DataTransmitToElasticStrategyFactory
    implements InitializingBean, StrategyFactory<BaseDataTransmitToElasticStrategy> {

    @Autowired
    private List<BaseDataTransmitToElasticStrategy> strategyList;

    private ConcurrentHashMap<String, BaseDataTransmitToElasticStrategy> strategyMap = new ConcurrentHashMap<>(3);

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
                if (StringUtils.isNotEmpty(schema) && StringUtils.isNotEmpty(table)) {
                    strategyMap.put(getStrategyKey(schema, table), strategy);
                }
            }
        });
    }

    @Override
    public BaseDataTransmitToElasticStrategy createStrategy(String schema, String table) {

        BaseDataTransmitToElasticStrategy strategy = strategyMap.get(getStrategyKey(schema, table));
        try {
            if (null != strategy) {
                return strategy;
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
