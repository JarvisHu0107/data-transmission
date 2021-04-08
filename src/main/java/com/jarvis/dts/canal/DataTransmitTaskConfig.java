package com.jarvis.dts.canal;

import lombok.Data;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/8 16:25
 * @Desc:
 **/
@Data
public class DataTransmitTaskConfig {

    public static final String DEFAULT_FACTORY_ALIAS_NAME = "default";
    /**
     * canal-server的实例配置
     */
    private CanalServerConfig serverConfig = new CanalServerConfig();

    /**
     * 策略工厂的实例名
     */
    private String factory = DEFAULT_FACTORY_ALIAS_NAME;

    /**
     * 数据传输任务的实例名
     */
    private String taskName;

}
