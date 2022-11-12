package com.clouddrive.main.rocketmq;

import com.clouddrive.util.RedisUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RocketMQMessageListener(topic = "file:downloadReturn", consumerGroup = "main_consumer")
public class DownloadReturnMessagesConsumer implements RocketMQListener<String> {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void onMessage(String s) {
        Map<String, Object> map = objectMapper.readValue(s, new TypeReference<Map>() {
        });
        redisUtil.addStringAndSetTimeOut("downloadReturn:" + map.get("hashId"), map.get("uploadId").toString(), 5);
    }
}
