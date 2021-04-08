package com.jarvis.dts.canal;

import java.util.List;

import com.jarvis.dts.canal.constant.RowEventTypeEnum;
import lombok.Data;

/**
 * @Author: Hu Xin
 * @Date: 2021/3/31 17:04
 * @Desc:
 **/
@Data
public class CanalRowEvent {

    /**
     * "数据行执行类型" 增删改查
     */
    private RowEventTypeEnum eventType;

    /**
     * 数据库名称
     */
    private String schemaName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 字段信息
     */
    private List<CanalColumn> columnList;

}
