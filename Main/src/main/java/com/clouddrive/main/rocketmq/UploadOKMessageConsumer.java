package com.clouddrive.main.rocketmq;

import com.clouddrive.main.service.FileLocalService;
import com.clouddrive.util.RedisUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RocketMQMessageListener(topic = "file:uploadOK", consumerGroup = "main_consumer")
public class UploadOKMessageConsumer implements RocketMQListener<String> {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    FileLocalService fileLocalService;
    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void onMessage(String s) {
        //读取文件中心发送过来的数据
        Map<String, Object> map = objectMapper.readValue(s, new TypeReference<Map>() {
        });
        String hash = map.get("hash").toString();
        String fileId = map.get("hash").toString();
        Integer size = Integer.valueOf(map.get("size").toString());

        //读取之前保存在Redis里面的文件相关数据
        String fileLocalData = redisUtil.getString("uploadData:" + hash + ":" + size);
        if (StringUtils.isEmpty(fileLocalData)) {
            //之前保存没了，这里可能的原因很多，但是既然没了那就直接终止这次upload
            return;
        }
        Map<String, Object> fileLocalDataMap = objectMapper.readValue(fileLocalData, new TypeReference<Map>() {
        });
        String name = fileLocalDataMap.get("folderId").toString();
        Integer folderId = Integer.valueOf(fileLocalDataMap.get("folderId").toString());
        Integer user = Integer.valueOf(fileLocalDataMap.get("user").toString());

        //数据持久化
        if (!fileLocalService.linkFileAndHash(user, name, size, folderId, hash + ":" + fileId)) {
            //持久化失败
            return;
        }
        //持久化成功（暂时不知道写啥
    }
}
