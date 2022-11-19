package com.clouddrive.file.service;

import com.clouddrive.common.core.flag.FileUploadState;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    FileUploadState UploadFile(String uploadId, Integer partId, MultipartFile file) throws IOException;

    void removeTask(String uploadId) throws JsonProcessingException;
}
