package com.jarvis.dts.strategy;

/**
 * 策略基础类
 * 
 * @Author: Hu Xin
 * @Date: 2021/4/8 12:26
 * @Desc:
 **/
public class BaseDataTransmitStrategy<T> extends AbstractDataTransmitStrategy<T> {

    @Override
    public void update(T row) {
        // do nothing
    }

    @Override
    public void delete(T row) {
        // do nothing
    }

    @Override
    public void insert(T row) {
        // do nothing
    }
}
