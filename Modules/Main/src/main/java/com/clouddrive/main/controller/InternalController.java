package com.clouddrive.main.controller;

import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.main.service.FileLocalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@ResponseBody
@Controller
@RequestMapping("/System")
public class InternalController extends BaseController {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    FileLocalService fileLocalService;

    @GetMapping("UploadOK")
    Boolean UploadOK(HttpServletResponse response, @RequestParam String uploadId, @RequestParam String fileId) throws JsonProcessingException {
        String fileLocalData = redisUtil.getString("uploadData:" + uploadId);
        if (StringUtils.isEmpty(fileLocalData)) {
            //之前保存没了，这里可能的原因很多，但是既然没了那就直接终止这次upload
            return false;
        }
        Map<String, Object> fileLocalDataMap = objectMapper.readValue(fileLocalData, new TypeReference<Map>() {
        });
        String name = fileLocalDataMap.get("name").toString();
        Integer folderId = Integer.valueOf(fileLocalDataMap.get("folderId").toString());
        Integer user = Integer.valueOf(fileLocalDataMap.get("user").toString());
        Long size = Long.valueOf(fileLocalDataMap.get("size").toString());

        //数据持久化
        try {
            fileLocalService.linkFileAndHash(user, name, size, folderId, fileId);
        } catch (RuntimeException e) {
            //持久化失败
            return false;
        }
        return true;
    }
}
