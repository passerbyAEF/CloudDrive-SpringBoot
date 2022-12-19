package com.clouddrive.main.controller;

import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.filecore.dto.RecycleDTO;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.common.security.util.UserUtil;
import com.clouddrive.main.service.FileListService;
import com.clouddrive.main.service.FileLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@ResponseBody
@RequestMapping("/External/Recycle")
public class RecycleController extends BaseController {

    @Autowired
    FileListService fileListService;
    @Autowired
    FileLocalService fileLocalService;

    @GetMapping("RecycleList")
    ReturnMode<Object> RecycleList(HttpServletResponse response) {
        UserMode user = UserUtil.getUser();
        return OK(fileListService.getRecycleList(user));
    }

    @PostMapping("Recovery")
    ReturnMode<Object> Recovery(HttpServletResponse response, @RequestBody RecycleDTO data) {
        UserMode user = UserUtil.getUser();
        if (!fileLocalService.RecoveryFile(user, data.getFileId())) {
            return Error();
        }
        return OK();
    }
}
