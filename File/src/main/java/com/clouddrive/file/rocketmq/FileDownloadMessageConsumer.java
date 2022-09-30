package com.clouddrive.file.rocketmq;

import com.clouddrive.util.RedisUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RocketMQMessageListener(topic = "file:download", consumerGroup = "file_consumer")
public class FileDownloadMessageConsumer implements RocketMQListener<String> {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RocketMQTemplate rocketMQTemplate;
    @Value("${CloudDrive.SavePath}")
    String fileSavePath;

    @SneakyThrows
    @Override
    public void onMessage(String s) {
        Map<String, Object> map = objectMapper.readValue(s, new TypeReference<Map>() {
        });

        String[] hash = map.get("hash").toString().split(":");
        String hashFolder = hash[0];
        String fileId = hash[1];
        File file = new File(fileSavePath + "\\" + hashFolder + "\\" + fileId);
        String downloadId = UUID.randomUUID().toString();
        String mess = String.format("{\"hash\":\"%s\",\"FileId\":\"%s\",\"size\":\"%s\"}", hash, fileId, file.length());

        redisUtil.addStringAndSetTimeOut(downloadId, mess, 5);

        rocketMQTemplate.asyncSend("file:downloadReturn", downloadId, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("已记录Download");
                log.info(downloadId + "已发送");
            }

            @SneakyThrows
            @Override
            public void onException(Throwable throwable) {
                log.info(downloadId + "发送失败，重新发送");
                Thread.sleep(1000);
                rocketMQTemplate.asyncSend("file:uploadReturn", downloadId, this);
            }
        });
    }
}
