package com.clouddrive.main.service;

import com.clouddrive.common.security.domain.UserMode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

//主要承接需要与文件中心交换数据的操作
public interface FileCoreService {

    String Upload(UserMode user, String name, int folderId, String hash, long size) throws JsonProcessingException;

    String getUploadFlag(String flag);

    String Download(UserMode user, int fileId) throws IOException;

    String getDownloadFlag(String flag);
}
