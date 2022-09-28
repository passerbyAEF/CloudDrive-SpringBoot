package com.clouddrive.file;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class FileApplicationTests {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Test
    void contextLoads() {
        rocketMQTemplate.convertAndSend("test", "Hello, World!");
    }

}
