package com.clouddrive.file.controller;

import com.clouddrive.file.flag.FileUploadState;
import com.clouddrive.file.service.UploadService;
import com.clouddrive.util.BaseController;
import com.clouddrive.util.ReturnMode;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class FileController extends BaseController {

    @Autowired
    UploadService uploadService;

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

}
