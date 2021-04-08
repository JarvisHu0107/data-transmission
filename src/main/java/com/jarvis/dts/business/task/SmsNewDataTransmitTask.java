package com.jarvis.dts.business.task;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.jarvis.dts.canal.task.BaseDataTransmitTask;

import lombok.extern.slf4j.Slf4j;

/**
 * sms-new库，数据传输任务
 *
 * @Author: Hu Xin
 * @Date: 2021/3/30 19:19
 * @Desc:
 **/
@Component("smsNew")
@Slf4j
public class SmsNewDataTransmitTask extends BaseDataTransmitTask {

    private static final Pattern pattern = Pattern.compile("[0-9]*");

    private static final String[] SCHEMA_SUFFIX = new String[] {"local", "test", "dev", "prod"};

    /**
     * 表名 最后一个_的后面是纯数字，则去掉
     *
     * @param source
     * @return
     */
    @Override
    protected String customizeTableConverted(String source) {
        int lastIndex = source.lastIndexOf("_");
        if (lastIndex == -1) {
            return source;
        }
        try {
            String suffix = source.substring(lastIndex + 1);
            Matcher matcher = pattern.matcher(suffix);
            if (matcher.matches()) {
                String target = source.substring(0, lastIndex);
                return target;
            }
        } catch (Exception e) {
            log.error("class:SmsNewSyncTask,method:customizeTableConverted,表名[" + source + "]转化出错", e);
        }
        return source;
    }

    /**
     * 库名 最后一个_的后面如果是prod dev test local结尾，则去掉
     *
     * @param source
     * @return
     */
    @Override
    protected String customizeSchemaConverted(String source) {
        int lastIndex = source.lastIndexOf("_");
        if (lastIndex == -1) {
            return source;
        }
        String suffix = source.substring(lastIndex + 1);
        if (Arrays.stream(SCHEMA_SUFFIX).parallel().anyMatch(suffix::equalsIgnoreCase)) {
            String target = source.substring(0, lastIndex);
            return target;
        }

        return source;
    }
}
