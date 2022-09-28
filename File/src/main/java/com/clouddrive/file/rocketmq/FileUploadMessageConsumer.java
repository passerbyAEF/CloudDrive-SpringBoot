package com.clouddrive.file.rocketmq;


import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "test", consumerGroup = "testGroup")
public class FileUploadMessageConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String s) {

    }
}
