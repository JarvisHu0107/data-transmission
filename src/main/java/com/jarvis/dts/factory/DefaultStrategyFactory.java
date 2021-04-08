package com.jarvis.dts.factory;

import org.springframework.stereotype.Component;

import com.jarvis.dts.strategy.DefaultDataTransmitStrategy;

/**
 * 默认策略工厂-生产默认策略
 * 
 * @Author: Hu Xin
 * @Date: 2021/4/2 11:03
 * @Desc:
 **/
@Component("default")
public class DefaultStrategyFactory implements StrategyFactory<DefaultDataTransmitStrategy> {

    @Override
    public DefaultDataTransmitStrategy createStrategy(String schema, String table) {
        return new DefaultDataTransmitStrategy();
    }
}
