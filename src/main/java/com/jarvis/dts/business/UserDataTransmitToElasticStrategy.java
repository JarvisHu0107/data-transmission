package com.jarvis.dts.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jarvis.dts.annotation.Strategy;
import com.jarvis.dts.business.doc.UserDoc;
import com.jarvis.dts.business.entity.User;
import com.jarvis.dts.business.mapstruct.UserDocStructMapper;
import com.jarvis.dts.business.repository.UserDocRepository;
import com.jarvis.dts.strategy.BaseDataTransmitToElasticStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理user表数据
 * 
 * @Author: Hu Xin
 * @Date: 2021/4/1 21:41
 * @Desc:
 **/
@Strategy(schema = "test", table = "user", pk = {"id"})
@Component
@Slf4j
public class UserDataTransmitToElasticStrategy extends BaseDataTransmitToElasticStrategy<User> {

    @Autowired
    private UserDocStructMapper userDocStructMapper;

    @Autowired
    private UserDocRepository repository;

    @Override
    public void update(User row) {
        UserDoc userDoc = userDocStructMapper.convertEntity2Doc(row);
        repository.save(userDoc);
        log.warn("UserDataTransmitToElasticStrategy,update:{}", row);
    }

    @Override
    public void delete(User row) {
        repository.deleteById(row.getId());
        log.warn("UserDataTransmitToElasticStrategy,delete:{}", row);
    }

    @Override
    public void insert(User row) {
        UserDoc userDoc = userDocStructMapper.convertEntity2Doc(row);
        repository.save(userDoc);
        log.warn("UserDataTransmitToElasticStrategy,insert:{}", row);
    }

}
