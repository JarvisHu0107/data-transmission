package com.jarvis.dts.canal.task;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.MDC;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.jarvis.dts.canal.CanalColumn;
import com.jarvis.dts.canal.CanalRowEvent;
import com.jarvis.dts.canal.CanalServerConfig;
import com.jarvis.dts.canal.constant.RowEventTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Hu Xin
 * @Date: 2021/3/30 19:22
 * @Desc:
 **/
@Slf4j
public abstract class AbstractDataTransmitTask {

    protected static final String SEP = SystemUtils.LINE_SEPARATOR;

    private AtomicBoolean running = new AtomicBoolean(false);

    private CanalConnector connector;

    private CanalServerConfig canalServerConfig;

    private DataTransmitExecutorManager executorManager;

    protected void setCanalServerConfig(@NotNull CanalServerConfig canalServerConfig) {
        this.canalServerConfig = canalServerConfig;
        this.connector = this.createConnector(canalServerConfig);
    }

    protected void setExecutorManager(DataTransmitExecutorManager executorManager) {
        this.executorManager = executorManager;
    }

    private CanalConnector createConnector(CanalServerConfig canalServerConfig) {
        return CanalConnectors.newSingleConnector(
            new InetSocketAddress(canalServerConfig.getIp(), canalServerConfig.getPort()),
            canalServerConfig.getDestination(), canalServerConfig.getUsername(), canalServerConfig.getPassword());
    }

    private void doConnect() {
        // 注意：是否自动化解析Entry对象,如果设置为true,则不会自动解析，需要关闭
        // ((SimpleCanalConnector) connector).setLazyParseEntry(true);
        connector.connect();
        // 订阅哪些表
        if (StringUtils.isNotEmpty(canalServerConfig.getClientSubscribe())) {
            connector.subscribe(canalServerConfig.getClientSubscribe());
        } else {
            connector.subscribe();
        }
        connector.rollback();

    }

    /**
     * 任务执行主体
     */
    public void start() {
        int batchSize = canalServerConfig.getBatchSize();
        String destination = canalServerConfig.getDestination();
        running.set(true);
        log.info("task:{} had been started...", this.getClass());
        while (running.get()) {
            try {
                MDC.put("destination", destination);
                doConnect();
                while (running.get()) {
                    // 获取指定数量的数据
                    Message message = connector.getWithoutAck(batchSize);
                    if (null == message) {
                        log.debug("message is null...");
                    } else {
                        long batchId = message.getId();
                        int size = message.getEntries().size();
                        if (batchId == -1 || size == 0) {
                            log.debug("there is no entries needed to be processed,batchId is {} or size is {}...",
                                batchId, size);
                            // 休眠5秒
                            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(5));
                        } else {
                            List<CanalRowEvent> rowEventList = parseMessage(message);
                            doProcess(rowEventList);
                        }
                        if (batchId != -1) {
                            // 同步提交确认
                            connector.ack(batchId);
                        }
                    }
                }
            } catch (Throwable e) {
                log.error("dts task 【" + this.getClass() + "】 process error!", e);
                connector.rollback(); // 处理失败, 回滚数据
            } finally {
                connector.disconnect();
                MDC.remove("destination");
            }
        }
        log.info("task:{} had been ended...", this.getClass());
        executorManager.countDown();

    }

    /**
     * 解析一次拉取数据的响应，只关心数据的"增删改"
     *
     * @param message
     * @return
     */
    private List<CanalRowEvent> parseMessage(Message message) {

        List<CanalEntry.Entry> entries = message.getEntries();
        List<CanalRowEvent> rowEventList = new ArrayList<>();
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                // 事务操作跳过
                continue;
            }

            if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                CanalEntry.RowChange rowChage = null;
                try {
                    rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                } catch (Exception e) {
                    throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
                }
                CanalEntry.EventType eventType = rowChage.getEventType();
                // 查询语句跳过
                if (eventType == CanalEntry.EventType.QUERY || rowChage.getIsDdl()) {
                    log.debug(" sql ----> " + rowChage.getSql() + SEP);
                    continue;
                }
                log.debug("schemaName:{},sourceTableName:{}", entry.getHeader().getSchemaName(),
                    entry.getHeader().getTableName());
                /**
                 * 行解析
                 */
                for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                    CanalRowEvent rowEvent = new CanalRowEvent();
                    rowEvent.setSchemaName(customizeSchemaConverted(entry.getHeader().getSchemaName()));
                    rowEvent.setTableName(customizeTableConverted(entry.getHeader().getTableName()));
                    List<CanalColumn> columns = null;
                    if (eventType == CanalEntry.EventType.DELETE) {
                        // DELETE
                        rowEvent.setEventType(RowEventTypeEnum.DELETE);
                        columns = parseColumns(rowData.getBeforeColumnsList());
                    } else if (eventType == CanalEntry.EventType.INSERT) {
                        // INSERT
                        rowEvent.setEventType(RowEventTypeEnum.INSERT);
                        columns = parseColumns(rowData.getAfterColumnsList());
                    } else {
                        // UPDATE
                        rowEvent.setEventType(RowEventTypeEnum.UPDATE);
                        columns = parseColumns(rowData.getAfterColumnsList());
                    }
                    rowEvent.setColumnList(columns);
                    rowEventList.add(rowEvent);
                }
            }
        }

        return rowEventList;
    }

    /**
     * 拉取的数据如何使用，由子类实现。
     *
     * @param rows
     */
    protected abstract void doProcess(List<CanalRowEvent> rows);

    /**
     * 停止任务
     */
    public void stop() {
        running.set(false);
    }

    /**
     * 列解析
     *
     * @param columns
     * @return
     */
    private List<CanalColumn> parseColumns(List<CanalEntry.Column> columns) {
        List<CanalColumn> columnList = new ArrayList<>();
        for (CanalEntry.Column each : columns) {
            CanalColumn column = new CanalColumn();
            // StringBuilder printBuilder = new StringBuilder();
            /**
             * 字段名和值
             */
            column.setColumnName(each.getName());
            column.setColumnValue(each.getValue());
            try {
                /**
                 * 特殊类型的字段值进行转化
                 */
                if (StringUtils.containsIgnoreCase(each.getMysqlType(), "BLOB")
                    || StringUtils.containsIgnoreCase(each.getMysqlType(), "BINARY")) {
                    // get value bytes
                    String valueConverted = new String(each.getValue().getBytes("ISO-8859-1"), "UTF-8");
                    // printBuilder.append(each.getName() + " : " + valueConverted);

                    column.setColumnValue(valueConverted);
                } else {
                    // printBuilder.append(each.getName() + " : " + each.getValue());
                }
            } catch (UnsupportedEncodingException e) {
                log.error("parseColumns error" + each, e);
            }
            /**
             * 字段类型
             */
            column.setMysqlType(each.getMysqlType());
            // printBuilder.append(" type=" + column.getMysqlType());
            /**
             * 字段属性
             */
            if (each.getUpdated()) {
                column.setUpdated(true);
                // printBuilder.append(" update=" + each.getUpdated());
            }
            if (each.getIsKey()) {
                column.setPk(true);
                // printBuilder.append(" PK=" + each.getIsKey());
            }
            // printBuilder.append(SEP);
            // log.info(printBuilder.toString());

            columnList.add(column);
        }
        return columnList;
    }

    /**
     * 自定义表名转化
     */
    protected String customizeTableConverted(String source) {
        return source;
    }

    /**
     * 自定义库名转化
     */
    protected String customizeSchemaConverted(String source) {
        return source;
    }

    public void init(CanalServerConfig config) {
        setCanalServerConfig(config);
    }

}
