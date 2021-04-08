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

    private CanalServerConfig serverConfig = new CanalServerConfig();

    private String factory = DEFAULT_FACTORY_ALIAS_NAME;

    private String taskName;

}
