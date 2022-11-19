package com.clouddrive.file.rocketmq;


import com.clouddrive.common.redis.util.RedisUtil;
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

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
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
        RandomAccessFile file = new RandomAccessFile(fileBufferPath + "/" + hash, "rw");
        //file.setLength(Long.getLong(size));
        file.setLength(1);
        file.close();

        String uploadId = UUID.randomUUID().toString();
        Map<String, String> reMap = new HashMap<>();
        reMap.put("hash", hash);
        reMap.put("size", size);
        reMap.put("uploadId", uploadId);
        String reJsonStr = objectMapper.writeValueAsString(reMap);

        //获取id并保存入Redis中，wrote记录当前已写入的字节范围
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hash", hash);
        dataMap.put("size", size);
        dataMap.put("wrote", new ArrayList<String>());
        String dataJsonStr = objectMapper.writeValueAsString(dataMap);
//        String mess = String.format("{\"hash\":\"%s\",\"size\":\"%s\",\"wrote\":[]}", hash, size);
        redisUtil.addStringAndSetTimeOut(uploadId, dataJsonStr, 5);

        rocketMQTemplate.asyncSend("file:uploadReturn", reJsonStr, new SendCallback() {
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
