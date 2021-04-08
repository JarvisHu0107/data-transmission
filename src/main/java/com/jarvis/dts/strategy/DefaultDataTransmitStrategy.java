package com.jarvis.dts.strategy;

import org.springframework.stereotype.Component;

import com.jarvis.dts.canal.CanalRowEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认处理策略
 * 
 * @Author: Hu Xin
 * @Date: 2021/4/2 11:04
 * @Desc: 仅打印
 **/
@Component
@Slf4j
public class DefaultDataTransmitStrategy implements DataTransmitStrategy<CanalRowEvent> {

    @Override
    public void update(CanalRowEvent row) {
        log.warn("默认传输策略，update:{}", row);
    }

    @Override
    public void delete(CanalRowEvent row) {
        log.warn("默认传输策略，delete:{}", row);
    }

    @Override
    public void insert(CanalRowEvent row) {
        log.warn("默认传输策略，insert:{}", row);
    }
}
