package com.jarvis.dts.business.doc;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import lombok.Data;

/**
 * @Author: Hu Xin
 * @Date: 2021/3/29 22:46
 * @Desc:
 **/
@Data
@Mapping(mappingPath = "/mapping/sms-msg.json")
@Setting(settingPath = "/setting/semicolon.json")
@Document(indexName = "sms_msg", type = "_doc")
public class SmsMsgDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ES的唯一ID (同业务唯一ID)
     */
    @Id
    private Long id;
    /**
     * 业务唯一ID（雪花算法生成）
     */
    @Field(name = "msg_id", type = FieldType.Long)
    private Long msgId;


    /**
     * 用户消息唯一标识
     */
    @Field(name = "user_msg_id", type = FieldType.Keyword)
    private String userMsgId;

    /**
     * cmpp网关response的msgId，如果是长短信，逗号分割
     */
    @Field(name = "gateway_msg_id", type = FieldType.Text, analyzer = "semicolon_analyzer", searchAnalyzer = "semicolon_analyzer")
    private String gatewayMsgId;


    /**
     * 用户id
     */
    @Field(name = "user_id", type = FieldType.Long)
    private Long userId;

    /**
     * 机构id
     */
    @Field(name = "institution_id", type = FieldType.Long)
    private Long institutionId;

    /**
     * app id
     */
    @Field(name = "app_id", type = FieldType.Long)
    private Long appId;

    /**
     * 运营商
     */
    @Field(name = "sp", type = FieldType.Keyword)
    private String sp;

    /**
     * 电话号码
     */
    @Field(name = "telephone", type = FieldType.Keyword)
    private String telephone;

    /**
     * 计费条数
     */
    @Field(name = "charging_count", type = FieldType.Integer)
    private Integer chargingCount;

    /**
     * 消息内容
     */
    @Field(name = "msg_content", type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String msgContent;

    /**
     * 发送时间
     */
    @Field(name = "send_time", type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date sendTime;

    /**
     * 发送状态 0：待发送，1：发送中，2：已发送，3：发送失败，4：无可用通道，5：目标号码在黑名单中，6：短信内容含敏感词
     */
    @Field(name = "send_status", type = FieldType.Keyword)
    private String sendStatus;

    /**
     * 发送状态详情，如：错误信息
     */
    @Field(name = "send_detail", type = FieldType.Text, analyzer = "semicolon_analyzer", searchAnalyzer = "semicolon_analyzer")
    private String sendDetail;

    /**
     * 状态报告状态 0：等待状态报告，1：成功、2：失败
     */
    @Field(name = "report_status", type = FieldType.Keyword)
    private String reportStatus;


    /**
     * 状态报告详情，如：等待中、错误码等
     */
    @Field(name = "report_detail", type = FieldType.Text, analyzer = "semicolon_analyzer", searchAnalyzer = "semicolon_analyzer")
    private String reportDetail;


    /**
     * 通道id
     */
    @Field(name = "channel_id", type = FieldType.Long)
    private Long channelId;


    /**
     * 通道号
     */
    @Field(name = "sp_no", type = FieldType.Keyword)
    private String spNo;

    /**
     * 创建时间
     */
    @Field(name = "create_time", type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(name = "update_time", type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date updateTime;

    /**
     * 扩展号
     */
    @Field(name = "ext_no", type = FieldType.Keyword)
    private String extNo;

}
