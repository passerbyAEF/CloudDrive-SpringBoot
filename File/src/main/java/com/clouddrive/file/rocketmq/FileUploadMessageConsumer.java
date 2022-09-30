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
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RocketMQMessageListener(topic = "file:upload", consumerGroup = "file_consumer")
public class FileUploadMessageConsumer implements RocketMQListener<String> {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RocketMQTemplate rocketMQTemplate;
    @Value("${CloudDrive.SavePath}")
    String fileSavePath;
    @Value("${CloudDrive.BufferPath}")
    String fileBufferPath;

    @SneakyThrows
    @Override
    public void onMessage(String s) {
        Map<String, Object> map = objectMapper.readValue(s, new TypeReference<Map>() {
        });
        String hash = map.get("hash").toString();
        String size = map.get("size").toString();

//        if (!new File(fileSavePath).exists()) {
//            throw new FileNotFoundException(fileSavePath + "不存在！");
//        }

        //创建一个空文件占位
        RandomAccessFile file = new RandomAccessFile(fileBufferPath + "/" + "hash", "rw");
        file.setLength(Long.getLong(size));
        file.close();

        //获取id并保存入Redis中，wrote记录当前已写入的字节范围
        String uploadId = UUID.randomUUID().toString();
        String mess = String.format("{\"hash\":\"%s\",\"size\":\"%s\",\"wrote\":[]}", hash, size);
        redisUtil.addStringAndSetTimeOut(uploadId, mess, 5);

        rocketMQTemplate.asyncSend("file:uploadReturn", uploadId, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("已记录Upload");
                log.info(uploadId + "已发送");
            }

            @SneakyThrows
            @Override
            public void onException(Throwable throwable) {
                log.info(uploadId + "发送失败，重新发送");
                Thread.sleep(1000);
                rocketMQTemplate.asyncSend("file:uploadReturn", uploadId, this);
            }
        });
    }
}
