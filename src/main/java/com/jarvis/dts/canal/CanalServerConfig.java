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

    private String destination = "example";

    private String username = "canal";

    private String password = "canal";

    private int batchSize = 1024;

    /**
     * 客户端订阅schema table的规则，具体参考 https://github.com/alibaba/canal/wiki/FAQ
     */
    private String clientSubscribe;

    //
    // public CanalServerConfig(String ip, int port, String destination, String username, String password) {
    // this.ip = ip;
    // this.port = port;
    // this.destination = destination;
    // this.username = username;
    // this.password = password;
    // }
    //
    //
    // public CanalServerConfig() {
    //
    // }
    //
    // public CanalServerConfig(String ip, int port, String destination, String username, String password, int
    // batchSize) {
    // this(ip, port, destination, username, password);
    // this.batchSize = batchSize;
    // }
    //
    //
    // public CanalServerConfig(String ip, int port, String destination, String username, String password, int
    // batchSize, String clientSubscribe) {
    // this(ip, port, destination, username, password, batchSize);
    // this.clientSubscribe = clientSubscribe;
    // }

}
