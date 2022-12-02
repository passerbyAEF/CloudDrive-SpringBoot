package com.clouddrive.modules.file.service;

import com.clouddrive.common.core.flag.FileUploadState;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface UploadService {
    FileUploadState UploadFile(String uploadId, Integer partId, MultipartFile file) throws IOException;

    void removeTask(String uploadId) throws JsonProcessingException;

    Map<String, String> createUpload(String hash, Long size) throws IOException;
}
