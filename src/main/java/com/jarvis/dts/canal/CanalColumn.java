package com.jarvis.dts.canal;

import lombok.Data;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/1 11:19
 * @Desc:
 **/
@Data
public class CanalColumn {

    /**
     * 数据库字段类型
     */
    private String mysqlType;

    /**
     * 字段名称 与 数据库中一致
     */
    private String columnName;

    /**
     * 字段值
     */
    private String columnValue;

    /**
     * 是否为主键
     */
    private boolean pk = false;

    /**
     * 是否被更新
     */
    private boolean updated = false;

}
