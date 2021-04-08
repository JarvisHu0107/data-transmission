package com.jarvis.dts.business.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.jarvis.dts.business.doc.UserDoc;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/7 16:43
 * @Desc:
 **/
@Repository
public interface UserDocRepository extends ElasticsearchRepository<UserDoc, Long> {}
