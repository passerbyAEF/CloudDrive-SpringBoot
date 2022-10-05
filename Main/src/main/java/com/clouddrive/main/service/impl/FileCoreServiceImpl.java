package com.clouddrive.main.service.impl;

import com.clouddrive.main.service.FileCoreService;
import com.clouddrive.model.data.UserMode;
import com.clouddrive.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FileCoreServiceImpl implements FileCoreService {

    @Autowired
    private RocketMQTemplate updateTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String Upload(UserMode user, String name, int folderId, String hash, long size) throws JsonProcessingException {
        //打包数据发送给文件中心
        Map<String, Object> map = new HashMap<>();
        map.put("hash", hash);
        map.put("size", size);
        String json = objectMapper.writeValueAsString(map);
        rocketMQTemplate.asyncSend("file:upload", json, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {

            }

            @Override
            public void onException(Throwable throwable) {
                //发送失败，不重复发送了，直接放弃
                redisUtil.addStringAndSetTimeOut("uploadReturn:" + hash + ":" + size, "error", 5);
            }
        });

        //将相关数据暂存入Redis，等到之后文件中心完全接收完毕文件后再读取
        //Redis用hash+文件长度做名称，避免重名
        map = new HashMap<>();
        map.put("name", name);
        map.put("folderId", folderId);
        map.put("user", user.getId());
        redisUtil.addStringAndSetTimeOut("uploadData:" + map.get("hash") + ":" + size, objectMapper.writeValueAsString(map), 24, TimeUnit.HOURS);

        return hash;
    }

    @Override
    public String getUploadFlag(String hash, Long size) {
        String reStr = redisUtil.getString("uploadReturn:" + hash + ":" + size);
        if (reStr != null) {
            redisUtil.removeString("uploadReturn:" + hash + ":" + size);
        }
        return reStr;
    }


    @Override
    public String Download(UserMode user, int fileId) {
        return null;
    }
}
