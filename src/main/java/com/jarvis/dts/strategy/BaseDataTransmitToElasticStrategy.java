package com.jarvis.dts.strategy;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/8 12:26
 * @Desc:
 **/
public class BaseDataTransmitToElasticStrategy<T> extends AbstractDataTransmitStrategy<T> {

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
