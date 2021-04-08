package com.jarvis.dts.business.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.jarvis.dts.business.doc.SmsMsgDoc;

/**
 * @Author: Hu Xin
 * @Date: 2021/3/29 22:53
 * @Desc:
 **/
@Repository
public interface SmsMsgDocRepository extends ElasticsearchRepository<SmsMsgDoc, Long> {

}
