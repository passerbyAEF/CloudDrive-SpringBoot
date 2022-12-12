package com.clouddrive.main.service;

import com.clouddrive.common.filecore.dto.DownloadDataDTO;
import com.clouddrive.common.security.domain.UserMode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Map;

//主要承接需要与文件中心交换数据的操作
public interface FileCoreService {

    Map<String,String> Upload(UserMode user, String name, int folderId, String hash, long size) throws JsonProcessingException;

    @Deprecated
    String getUploadFlag(String flag);

    String Download(UserMode user, int fileId) throws IOException;

    DownloadDataDTO getDownloadFlag(String flag) throws JsonProcessingException;
}
