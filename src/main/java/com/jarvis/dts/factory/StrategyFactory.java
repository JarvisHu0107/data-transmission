package com.jarvis.dts.factory;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/2 10:17
 * @Desc:
 **/
public interface StrategyFactory<T> {

    T createStrategy(String schema, String table);

}
