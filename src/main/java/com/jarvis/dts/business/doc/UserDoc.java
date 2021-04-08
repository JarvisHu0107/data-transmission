package com.jarvis.dts.business.doc;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import lombok.Data;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/7 16:39
 * @Desc:
 **/
@Data
@Mapping(mappingPath = "/mapping/user.json")
@Document(indexName = "user", type = "_doc")
public class UserDoc implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * ES的唯一ID (同业务唯一ID)
     */
    @Id
    private Long id;

    @Field(name = "name", type = FieldType.Keyword)
    private String name;

    @Field(name = "age", type = FieldType.Integer)
    private Integer age;


}
