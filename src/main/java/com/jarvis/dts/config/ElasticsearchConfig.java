package com.jarvis.dts.config;

import java.util.List;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;

/**
 * @Author: Hu Xin
 * @Date: 2021/4/6 22:46
 * @Desc:
 **/
@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Autowired
    RestClientProperties restClientProperties;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        List<String> uris = restClientProperties.getUris();
        String[] uriArray = uris.toArray(new String[uris.size()]);
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(uriArray)
            .withBasicAuth(restClientProperties.getUsername(), restClientProperties.getPassword()).build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    @Override
    public EntityMapper entityMapper() {
        ElasticsearchEntityMapper entityMapper =
            new ElasticsearchEntityMapper(elasticsearchMappingContext(), new DefaultConversionService());
        entityMapper.setConversions(elasticsearchCustomConversions());

        return entityMapper;
    }

    /**
     * 配置使用ElasticsearchEntityMapper来解决字段映射的问题， 使用@Field的name属性，引入自带的字段别名映射能力。 如果不配置，则会使用"驼峰"格式的字段。
     *
     * @param client
     * @return
     */
    @Bean
    public ElasticsearchRestTemplate elasticsearchTemplate(RestHighLevelClient client, EntityMapper entityMapper) {
        ElasticsearchRestTemplate elasticsearchTemplate = new ElasticsearchRestTemplate(client, entityMapper);
        return elasticsearchTemplate;

    }
}
