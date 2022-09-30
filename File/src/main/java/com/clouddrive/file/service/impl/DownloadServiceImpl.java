package com.clouddrive.file.service.impl;

import com.clouddrive.file.service.DownloadService;
import com.clouddrive.util.RedisUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${CloudDrive.SavePath}")
    String fileSavePath;

    @Override
    public FileInputStream DownloadFile(String downloadId) throws IOException {
        String jsonStr = redisUtil.getString(downloadId);
        if (jsonStr == null)
            return null;
        Map<String, Object> map = objectMapper.readValue(jsonStr, new TypeReference<Map>() {
        });
        File file = new File(fileSavePath + "\\" + map.get("hash") + "\\" + map.get("fileId"));
        if (!file.exists()) {
            return null;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        return fileInputStream;
    }
}