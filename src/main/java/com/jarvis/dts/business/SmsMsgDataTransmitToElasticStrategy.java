package com.jarvis.dts.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jarvis.dts.annotation.Strategy;
import com.jarvis.dts.business.doc.SmsMsgDoc;
import com.jarvis.dts.business.entity.SmsMsg;
import com.jarvis.dts.business.mapstruct.SmsMsgDocStructMapper;
import com.jarvis.dts.business.repository.SmsMsgDocRepository;
import com.jarvis.dts.strategy.BaseDataTransmitToElasticStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理sms_msg表数据的策略
 * 
 * @Author: Hu Xin
 * @Date: 2021/4/1 21:41
 * @Desc:
 **/
@Strategy(schema = "sms_new", table = "sms_msg", pk = {"msg_id"})
@Component
@Slf4j
public class SmsMsgDataTransmitToElasticStrategy extends BaseDataTransmitToElasticStrategy<SmsMsg> {

    @Autowired
    private SmsMsgDocStructMapper smsMsgDocStructMapper;

    @Autowired
    private SmsMsgDocRepository repository;

    @Override
    public void update(SmsMsg row) {
        SmsMsgDoc smsMsgDoc = smsMsgDocStructMapper.convertEntity2Doc(row);
        repository.save(smsMsgDoc);
        log.warn("SmsMsgDataTransmitToElasticStrategy,update:{}", row);
    }

    @Override
    public void delete(SmsMsg row) {
        repository.deleteById(row.getMsgId());
        log.warn("SmsMsgDataTransmitToElasticStrategy,delete:{}", row);
    }

    @Override
    public void insert(SmsMsg row) {
        SmsMsgDoc smsMsgDoc = smsMsgDocStructMapper.convertEntity2Doc(row);
        repository.save(smsMsgDoc);
        log.warn("SmsMsgDataTransmitToElasticStrategy,insert:{}", row);
    }

    /**
     * 范围查询示例
     * 
     * @throws Exception
     */
    private void rangeQuery() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryBuilder queryBuilder = QueryBuilders.rangeQuery("send_time")
            .from(sdf.parse("2021-01-01 00:00:00").getTime()).to(sdf.parse("2021-05-01 00:00:00").getTime());
        Iterable<SmsMsgDoc> search = repository.search(queryBuilder);
        List<SmsMsgDoc> smsMsgDocList = new ArrayList<>();
        search.forEach(single -> smsMsgDocList.add(single));
        System.out.println(smsMsgDocList);

    }
}
