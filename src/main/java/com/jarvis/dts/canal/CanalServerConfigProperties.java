package com.jarvis.dts.canal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/8 16:00
 * @Desc:
 **/
@ConfigurationProperties(prefix = "canal-server.config")
@Component
@Data
public class CanalServerConfigProperties {

    private List<DataTransmitTaskConfig> mappings;

}
