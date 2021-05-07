package com.jarvis.dts.factory;

import java.util.ArrayList;
import java.util.List;

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
    public List<DefaultDataTransmitStrategy> createStrategy(String schema, String table) {
        List<DefaultDataTransmitStrategy> list = new ArrayList<>();
        list.add(new DefaultDataTransmitStrategy());
        return list;
    }
}
