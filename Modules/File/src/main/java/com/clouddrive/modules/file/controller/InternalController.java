package com.clouddrive.modules.file.controller;

import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.modules.file.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@ResponseBody
@Controller("/System")
public class InternalController extends BaseController {

    @Autowired
    UploadService uploadService;

    @GetMapping("CreateUpdate")
    Map<String, String> CreateUpdate(HttpServletResponse response, String hash, Long size) {
        try {
            return uploadService.createUpload(hash, size);
        } catch (IOException e) {
            e.printStackTrace();
            set500(response);
            return null;
        }
    }
}
