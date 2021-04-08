package com.jarvis.dts.canal;

import lombok.Data;

/**
 * 连接canal server端的配置
 *
 * @Author: Hu Xin
 * @Date: 2021/3/30 19:42
 * @Desc:
 **/
@Data
public class CanalServerConfig {

    private String ip = "127.0.0.1";

    private int port = 11111;

    /**
     * canal-server中单个实例对应的目录名字
     */
    private String destination = "example";

    private String username = "canal";

    private String password = "canal";

    /**
     * 一次拉取多少数据
     */
    private int batchSize = 1024;

    /**
     * 客户端订阅schema table的规则，具体参考 https://github.com/alibaba/canal/wiki/FAQ
     */
    private String clientSubscribe;

}
