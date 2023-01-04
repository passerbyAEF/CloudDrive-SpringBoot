package com.clouddrive.main.controller;

import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.core.exception.AuthException;
import com.clouddrive.common.core.exception.SQLException;
import com.clouddrive.common.filecore.dto.RecycleDTO;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.common.security.util.UserUtil;
import com.clouddrive.main.service.FileListService;
import com.clouddrive.main.service.FileLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    ReturnMode<Object> Recovery(HttpServletResponse response, @RequestBody List<RecycleDTO> data) {
        UserMode user = UserUtil.getUser();
        try {
            fileLocalService.RecoveryRecycle(user, data);
        } catch (SQLException e) {
            return Error();
        } catch (AuthException e) {
            return Error(e.getMessage());
        }
        return OK();
    }

    @PostMapping("Delete")
    ReturnMode<Object> Delete(HttpServletResponse response, @RequestBody List<RecycleDTO> data) {
        UserMode user = UserUtil.getUser();
        try {
            fileLocalService.DeleteRecycle(user, data);
        } catch (SQLException e) {
            return Error();
        } catch (AuthException e) {
            return Error(e.getMessage());
        }
        return OK();
    }
}
