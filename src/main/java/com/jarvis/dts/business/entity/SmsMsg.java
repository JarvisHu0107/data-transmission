package com.jarvis.dts.business.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息发送表
 * </p>
 *
 * @author huxin
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SmsMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    private Long id;

    /**
     * 业务唯一ID（雪花算法生成）
     */
    private Long msgId;

    /**
     * 用户消息唯一标识
     */
    private String userMsgId;

    /**
     * cmpp网关response的msgId，如果是长短信，逗号分割
     */
    private String gatewayMsgId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 机构id
     */
    private Long institutionId;

    /**
     * app id
     */
    private Long appId;

    /**
     * 运营商
     */
    private String sp;

    /**
     * 电话号码
     */
    private String telephone;

    /**
     * 计费条数
     */
    private Integer chargingCount;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 发送状态 0：待发送，1：发送中，2：已发送，3：发送失败，4：无可用通道，5：目标号码在黑名单中，6：短信内容含敏感词
     */
    private String sendStatus;

    /**
     * 发送状态详情，如：错误信息
     */
    private String sendDetail;

    /**
     * 状态报告状态 0：等待状态报告，1：成功、2：失败
     */
    private String reportStatus;

    /**
     * 状态报告详情，如：等待中、错误码等
     */
    private String reportDetail;

    /**
     * 通道id
     */
    private Long channelId;

    /**
     * 通道号
     */
    private String spNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 扩展号
     */
    private String extNo;

}
