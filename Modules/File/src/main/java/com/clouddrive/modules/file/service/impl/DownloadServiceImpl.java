package com.clouddrive.modules.file.service.impl;

import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.modules.file.service.DownloadService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Service
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${clouddrive.save-path}")
    String fileSavePath;

    @Override
    public FileInputStream DownloadFile(String downloadId) throws IOException {
        String jsonStr = redisUtil.getString("downloadTask:" + downloadId);
        if (jsonStr == null)
            return null;
        Map<String, Object> map = objectMapper.readValue(jsonStr, new TypeReference<Map>() {
        });
        File file = new File(Paths.get(fileSavePath, map.get("hash").toString(), map.get("fileId").toString()).toString());
        if (!file.exists()) {
            return null;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        return fileInputStream;
    }
}
