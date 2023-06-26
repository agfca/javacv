package com.llrj.javacv.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * @author guo.fucheng
 * @version v1.0
 * @description 三方接口调用配置 RestTemplate
 * @date 2023-03-09
 * @copyright 2022 iwhalecloud . All rights reserved.
 */
@Configuration
public class RestConfig {

    /**
     * 三方接口调用 : @RefreshScope 根据配置中心配置, 刷新 readTimeout、connectTimeout
     */
    @Bean(name = "restTemplate")
    @Primary
    public RestTemplate restOperations() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // todo 配置时间
        requestFactory.setReadTimeout(10 * 1000);
        requestFactory.setConnectTimeout(5 * 1000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        // 使用 utf-8 编码集的 conver 替换默认的 conver（默认的 string conver 的编码集为 "ISO-8859-1"）
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter<?> converter = iterator.next();
            if (converter instanceof StringHttpMessageConverter) {
                iterator.remove();
            }
        }
        messageConverters.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}

