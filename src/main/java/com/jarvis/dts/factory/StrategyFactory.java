package com.jarvis.dts.factory;

import java.util.List;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/2 10:17
 * @Desc:
 **/
public interface StrategyFactory<T> {

    List<T> createStrategy(String schema, String table);

}
