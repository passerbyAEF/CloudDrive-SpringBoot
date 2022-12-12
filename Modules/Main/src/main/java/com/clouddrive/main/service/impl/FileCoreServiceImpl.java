package com.clouddrive.main.service.impl;

import com.clouddrive.common.filecore.domain.FileMode;
import com.clouddrive.common.filecore.dto.DownloadDataDTO;
import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.main.feign.FileCoreFeign;
import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.service.FileCoreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FileCoreServiceImpl implements FileCoreService {

    //    @Autowired
//    private RocketMQTemplate updateTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    FileCoreFeign fileCoreFeign;

    @Override
    public Map<String, String> Upload(UserMode user, String name, int folderId, String hash, long size) throws JsonProcessingException {
        Map<String, String> map = fileCoreFeign.CreateUpdate(hash, size);
        if (map == null) {
            return null;
        }

        String UploadID = map.get("uploadId");

        Map<String, Object> dataMap = new HashMap<>();
        //将相关数据暂存入Redis，等到之后文件中心完全接收完毕文件后再读取
        //Redis用hash+文件长度做名称，避免重名
        dataMap.put("name", name);
        dataMap.put("folderId", folderId);
        dataMap.put("size", size);
        dataMap.put("user", user.getId());
        redisUtil.addStringAndSetTimeOut("uploadData:" + UploadID, objectMapper.writeValueAsString(dataMap), 24, TimeUnit.HOURS);

        return map;
    }

    @Deprecated
    @Override
    public String getUploadFlag(String flag) {
        String reStr = redisUtil.getString("uploadReturn:" + flag);
        if (reStr == null) {
            return "wait";
        }
        redisUtil.removeString("uploadReturn:" + flag);
        return reStr;
    }


    @Override
    public String Download(UserMode user, int fileId) throws IOException {
        FileMode fileMode = fileMapper.selectById(fileId);
        if (fileMode == null || !fileMode.getUserId().equals(user.getId()))
            throw new IOException("文件不存在");
        String flag = fileMode.getHashId();
        Map<String, String> data = new HashMap<>();
        data.put("hash", fileMode.getHashId());
        rocketMQTemplate.asyncSend("file:download", objectMapper.writeValueAsString(data), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }

            @Override
            public void onException(Throwable throwable) {
                //发送失败，不重复发送了，直接放弃
                redisUtil.addStringAndSetTimeOut("downloadReturn:" + flag, "error", 5);
            }
        });
        return flag;
    }

    @Override
    public DownloadDataDTO getDownloadFlag(String flag) throws JsonProcessingException {
        String reStr = redisUtil.getString("downloadReturn:" + flag);
        DownloadDataDTO data=new DownloadDataDTO();
        if (reStr == null) {
            return data;
        }
        redisUtil.removeString("downloadReturn:" + flag);
        Map<String,String> map=objectMapper.readValue(reStr,new TypeReference<Map>() {
        });

        data.setReady(true);
        data.setNodeId(Long.parseLong(map.get("nodeId")));
        data.setDownloadID(map.get("downloadId"));

        return data;
    }
}
