package com.clouddrive.modules.file.rocketmq;

import com.clouddrive.common.id.feign.GetIDFeign;
import com.clouddrive.common.metadata.constant.WorkIDConstants;
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
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RocketMQMessageListener(topic = "file:download", consumerGroup = "file_consumer")
public class FileDownloadMessageConsumer implements RocketMQListener<String> {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RocketMQTemplate rocketMQTemplate;
    @Autowired
    GetIDFeign getIDFeign;

    @Value("${clouddrive.save-path}")
    String fileSavePath;

    @SneakyThrows
    @Override
    public void onMessage(String s) {
        Map<String, Object> map = objectMapper.readValue(s, new TypeReference<Map>() {
        });

        String[] hash = map.get("hash").toString().split("_");
        String hashFolder = hash[0];
        String fileId = hash[1];
        File file = new File(Paths.get(fileSavePath, hashFolder, fileId).toString());
        if (!file.exists()) {
            return;
        }
        Long downloadId = getIDFeign.getID(WorkIDConstants.DownloadID);
        String mess = String.format("{\"hash\":\"%s\",\"FileId\":\"%s\",\"size\":\"%s\"}", hash, fileId, file.length());

        redisUtil.addStringAndSetTimeOut("downloadTask:" + downloadId, mess, 5);

        Map<String, String> re = new HashMap<>();
        re.put("hashId", map.get("hash").toString());
        re.put("downloadId", downloadId.toString());
        re.put("nodeId", WorkIDConstants.NodeID.toString());
        rocketMQTemplate.asyncSend("centre:downloadReturn", objectMapper.writeValueAsString(re), new SendCallback() {
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
                rocketMQTemplate.asyncSend("file:downloadReturn", downloadId, this);
            }
        });
    }
}
