package com.jarvis.dts.strategy;

import com.jarvis.dts.canal.constant.RowEventTypeEnum;

/** 数据传输策略抽象类
 * @Author: Hu Xin
 * @Date: 2021/4/1 21:32
 * @Desc:
 **/
public abstract class AbstractDataTransmitStrategy<T> implements DataTransmitStrategy<T> {

    /**
     * 在转发给各自对应处理类之前，可以自定义对数据实体做一些处理
     *
     * @param data
     */
    protected void customizeProcessBeforeDispatch(RowEventTypeEnum eventType, Object data) {
        // do noting

    }

    @Override
    public void dispatchToMethod(RowEventTypeEnum eventType, Object instance) {
        customizeProcessBeforeDispatch(eventType, instance);
        if (eventType == RowEventTypeEnum.INSERT) {
            this.insert((T)instance);
        } else if (eventType == RowEventTypeEnum.UPDATE) {
            this.update((T)instance);
        } else if (eventType == RowEventTypeEnum.DELETE) {
            this.delete((T)instance);
        }
    }
}
