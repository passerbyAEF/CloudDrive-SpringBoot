package com.clouddrive.modules.file.service.impl;

import com.clouddrive.common.core.flag.FileUploadState;
import com.clouddrive.common.id.feign.GetIDFeign;
import com.clouddrive.common.metadata.constant.WorkIDConstants;
import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.modules.file.feign.MainServiceFeign;
import com.clouddrive.modules.file.service.UploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RocketMQTemplate rocketMQTemplate;
    @Autowired
    GetIDFeign getIDFeign;
    @Autowired
    MainServiceFeign mainServiceFeign;

    @Value("${clouddrive.save-path}")
    String fileSavePath;
    @Value("${clouddrive.buffer-path}")
    String fileBufferPath;

    //10MB
    private static final long partSize = 1024L * 1024 * 10;

    @Override
    public FileUploadState UploadFile(String uploadId, Integer partId, MultipartFile file) throws IOException {
        String uploadData = redisUtil.getString(uploadId);
        Map<String, Object> map = objectMapper.readValue(uploadData, new TypeReference<Map>() {
        });

        String hashStr = map.get("hash").toString();
        String sizeStr = map.get("size").toString();
        String fileBufferSavePath = Paths.get(fileBufferPath, hashStr + ":" + sizeStr).toString();

        RandomAccessFile randomAccessFile = new RandomAccessFile(fileBufferSavePath, "w");
        randomAccessFile.seek(partSize * partId);
        randomAccessFile.write(file.getBytes());
        randomAccessFile.close();

        List<String> strList = (List<String>) map.get("wrote");
        List<Range> rangeList = new ArrayList<>();
        for (String s : strList) {
            String[] sl = s.split("-");
            Range range = new Range();
            range.setStart(Long.parseLong(sl[0]));
            range.setEnd(Long.parseLong(sl[1]));
            rangeList.add(range);
        }
        rangeList.sort((x, y) -> (int) (x.getStart() - y.getStart()));

        rangeList = EditWaitList(partSize * partId, partSize * (partId + 1), rangeList);

        //如果已经全部输入完毕
        if (rangeList.size() == 1 && rangeList.get(0).getStart() == 0 && rangeList.get(0).getEnd() == Long.parseLong(map.get("size").toString())) {
            //判断MD5值
            File bufferFile = new File(fileBufferSavePath);
            String md5Hash = DigestUtils.md5Hex(new FileInputStream(bufferFile));
            if (!md5Hash.equals(hashStr)) {
                return FileUploadState.ERROR;
            }
            //检查是否已经有MD5碰撞
            String fileSaveFolderPath = Paths.get(fileSavePath, hashStr).toString();
            String fileSavePath = Paths.get(fileSaveFolderPath, sizeStr).toString();
            //检查是否有这个文件夹
            File fileSaveFolder = new File(fileSaveFolderPath);
            if (!fileSaveFolder.exists()) {
                //没有就建
                if (!fileSaveFolder.mkdir()) {
                    throw new IOException("文件系统错误！");
                }
            }
            //检查是否有这个文件
            File fileSave = new File(fileSavePath);
            if (!fileSave.exists()) {
                //没有就存到这个位置
                if (!bufferFile.renameTo(fileSave)) {
                    return FileUploadState.ERROR;
                }
            } else {
                //有？那就撞了，按照设计，这种情况能撞上，极极极极极极极极大可能就是一个文件，那就不管了
                bufferFile.delete();
            }
            redisUtil.removeString(uploadId);
            if (mainServiceFeign.UploadOK(uploadId, hashStr + ":" + sizeStr))
                return FileUploadState.OK;
            else
                return FileUploadState.ERROR;
        }
        //生成JSONArray字符串
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        for (Range range : rangeList) {
            stringBuilder.append('\"');
            stringBuilder.append(range.getStart());
            stringBuilder.append('-');
            stringBuilder.append(range.getStart());
            stringBuilder.append('\"');
            stringBuilder.append(',');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(']');
        //更新入redis
        String mess = String.format("{\"hash\":\"%s\",\"size\":\"%s\",\"wrote\":%s}", hashStr, sizeStr, stringBuilder);
        redisUtil.addStringAndSetTimeOut(uploadId, mess, 5);
        return FileUploadState.INCOMPLETE;
    }

    //用于出问题时清除相关数据
    @Override
    public void removeTask(String uploadId) throws JsonProcessingException {
        //删除文件并清除redis记录
        String uploadData = redisUtil.getString(uploadId);
        Map<String, Object> map = objectMapper.readValue(uploadData, new TypeReference<Map>() {
        });
        String hashStr = map.get("hash").toString();
        rocketMQTemplate.asyncSend("file:uploadERROR", hashStr, new SendCallback() {
            @Override
            public void onSuccess(SendResult var1) {
                log.info("file:uploadERROR:" + hashStr + "发送成功");
            }

            @SneakyThrows
            @Override
            public void onException(Throwable var1) {
                //RocketMQ发送失败了（可能是挂了），重复发送
                Thread.sleep(1000);
                rocketMQTemplate.asyncSend("file:uploadERROR", hashStr, this);
            }
        });
        File file = new File(fileSavePath + hashStr);
        file.delete();
        redisUtil.removeString(uploadId);
    }

    @Override
    public Map<String, String> createUpload(String hash, Long size) throws IOException {
        //创建一个空文件占位
        RandomAccessFile file = new RandomAccessFile(Paths.get(fileBufferPath, hash + ":" + size).toString(), "rw");
        //file.setLength(Long.getLong(size));
        file.setLength(1);
        file.close();

        Long uploadId = getIDFeign.getID(WorkIDConstants.UploadID);
        Map<String, String> reMap = new HashMap<>();
        reMap.put("uploadId", uploadId.toString());
        reMap.put("nodeId", WorkIDConstants.NodeID.toString());

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hash", hash);
        dataMap.put("size", size);
        dataMap.put("wrote", new ArrayList<String>());
        String dataJsonStr = objectMapper.writeValueAsString(dataMap);
//        String mess = String.format("{\"hash\":\"%s\",\"size\":\"%s\",\"wrote\":[]}", hash, size);
        redisUtil.addStringAndSetTimeOut(uploadId.toString(), dataJsonStr, 5);
        log.info("已记录Upload");

        return reMap;
    }

    boolean fileCheck(String path1, String path2) throws IOException {
        RandomAccessFile file1 = new RandomAccessFile(path1, "r");
        RandomAccessFile file2 = new RandomAccessFile(path2, "r");
        if (file1.length() == file2.length()) {
            return true;
        }
        //检查最后512个bit是否相等
//        if (file1.length() > 512) {
//            file1.seek(file1.length() - 512);
//        }
//        if (file2.length() > 512) {
//            file2.seek(file2.length() - 512);
//        }
//        while (file1.readByte() == file2.readByte()) ;
//        if (file1.getFilePointer() == file1.length() && file2.getFilePointer() == file2.length()) {
//            return true;
//        }
        return false;
    }

    //笨方法融合，但很好理解很有效果
    List<Range> EditWaitList(long start, long end, List<Range> waitList) {
        Range range = new Range();
        range.setStart(start);
        range.setEnd(end);
        waitList.add(range);
        waitList.sort((x, y) -> (int) (x.getStart() - y.getStart()));
        for (int i = 0; i < waitList.size() - 1; i++) {
            if (waitList.get(i).getEnd() >= waitList.get(i + 1).getStart()) {
                Range l = waitList.get(i);
                Range r = waitList.get(i + 1);
                l.setStart(l.start);
                l.setEnd(r.end);
                waitList.remove(i + 1);
                i--;
            }
        }
        return waitList;
    }

    @Data
    class Range {
        long start;
        long end;
    }
}
