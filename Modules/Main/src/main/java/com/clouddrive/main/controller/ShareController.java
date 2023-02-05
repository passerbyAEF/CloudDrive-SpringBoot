package com.clouddrive.main.controller;


import com.clouddrive.common.core.constant.HttpStatus;
import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.security.util.UserUtil;
import com.clouddrive.main.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ResponseBody
@RequestMapping("/External/Share")
public class ShareController extends BaseController {

    @Autowired
    FileShareService fileShareService;

    @GetMapping("List")
    ReturnMode<Object> list(HttpServletResponse response, Integer id, @RequestParam(defaultValue = "/") String path, @RequestParam(required = false) String secretKey) {
        if (!StringUtils.hasLength(secretKey) && !fileShareService.hasCipher(id)) {
            return setDataAndReturn(null, "请认证！", HttpStatus.UNAUTHORIZED);
        }
        return OK(fileShareService.getFileListForShare(id, path));
    }

    @GetMapping("getEntityId")
    ReturnMode<Object> getEntityId(HttpServletResponse response, Integer id) {
        return OK(fileShareService.getEntityId(id));
    }

    @GetMapping("Download")
    ReturnMode<Object> Download(HttpServletResponse response, Integer id, String path, String fileName, @RequestParam(required = false) String secretKey) throws IOException {
        if (!StringUtils.hasLength(secretKey) && !fileShareService.hasCipher(id)) {
            return setDataAndReturn(null, "请认证！", HttpStatus.UNAUTHORIZED);
        }
        try {
            return OK(fileShareService.DownloadShareFile(id,path,fileName));
        } catch (IOException e) {
            return Error(e.getMessage());
        }
    }

}
