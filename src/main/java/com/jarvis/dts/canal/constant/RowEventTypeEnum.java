package com.jarvis.dts.canal.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/1 11:25
 * @Desc:
 **/
@Getter
@AllArgsConstructor
public enum RowEventTypeEnum {

    /**
     * row执行的sql命令类型
     */
    INSERT(0, "插入"), DELETE(1, "删除"), UPDATE(2, "修改"), SELECT(3, "查询"),;

    @JsonValue
    private final int code;
    private final String msg;

    @JsonCreator
    public static RowEventTypeEnum getFromCode(int code) {
        for (RowEventTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
