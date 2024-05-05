package io.github.zyszero.phoenix.registry.http;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zyszero
 * @Date: 2024/4/1 21:10
 */
public interface HttpInvoker {

    Logger log = LoggerFactory.getLogger(HttpInvoker.class);

    HttpInvoker DEFAULT = new OkHttpInvoker(500);

    String post(String requestString, String url);

    String get(String url);


    @SneakyThrows
    static <T> T httpGet(String url, Class<T> clazz) {
        log.debug(" =====>>>>>> httpGet: " + url);
        String respJson = DEFAULT.get(url);
        log.debug(" =====>>>>>> response: " + respJson);
        return JSON.parseObject(respJson, clazz);
    }


    @SneakyThrows
    static <T> T httpPost(String requestString,String url, Class<T> clazz) {
        log.debug(" =====>>>>>> httpGet: " + url);
        String respJson = DEFAULT.post(requestString, url);
        log.debug(" =====>>>>>> response: " + respJson);
        return JSON.parseObject(respJson, clazz);
    }
}
