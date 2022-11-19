package com.clouddrive.file.controller;

import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.core.flag.FileUploadState;
import com.clouddrive.file.service.DownloadService;
import com.clouddrive.file.service.UploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Controller
public class FileController extends BaseController {

    @Autowired
    UploadService uploadService;
    @Autowired
    DownloadService downloadService;

    @PostMapping("Upload")
    ReturnMode<Object> Upload(HttpServletResponse response, String uploadId, Integer partId, MultipartFile file) throws JsonProcessingException {
        try {
            FileUploadState state = uploadService.UploadFile(uploadId, partId, file);

            if (state == FileUploadState.OK || state == FileUploadState.INCOMPLETE) {
                return OK();
            } else if (state == FileUploadState.ERROR) {
                uploadService.removeTask(uploadId);
                return Error("文件验证失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
            uploadService.removeTask(uploadId);
            return Error("Bad!");
        }
        return Error("Bad!");
    }

    @GetMapping("Download")
    void Download(HttpServletResponse response, String downloadId, String fileName) {
        try {
            FileInputStream fileInputStream = downloadService.DownloadFile(downloadId);
            if (fileInputStream == null)
                throw new IOException();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            response.setContentLength(fileInputStream.available());
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            servletOutputStream.flush();

            int bytesRead;
            for (byte[] buffer = new byte[4096]; (bytesRead = fileInputStream.read(buffer)) != -1; ) {
                servletOutputStream.write(buffer, 0, bytesRead);
                servletOutputStream.flush();
            }
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            Code500(response);
        }
    }

    void Code500(HttpServletResponse response) {
        response.setStatus(500);
    }
}
