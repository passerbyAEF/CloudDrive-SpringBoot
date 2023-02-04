package com.clouddrive.main.controller;

import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.core.exception.AuthException;
import com.clouddrive.common.core.exception.SQLException;
import com.clouddrive.common.filecore.dto.CreateShareDTO;
import com.clouddrive.common.filecore.dto.UploadShareDTO;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.common.security.util.UserUtil;
import com.clouddrive.main.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/External/Share/Control")
public class ShareControlController extends BaseController {

    @Autowired
    FileShareService fileShareService;

    @GetMapping("List")
    ReturnMode<Object> list(HttpServletResponse response) {
        UserMode user = UserUtil.getUser();
        return OK(fileShareService.getList(user));
    }

    @PostMapping("Create")
    ReturnMode<Object> CreateShare(HttpServletResponse response, @RequestBody @Valid CreateShareDTO data) {
        if ((data.getIsUseCipher() && !StringUtils.hasLength(data.getPwd())) || (data.getIsUseOverdue() && data.getOverdueTime() == null)) {
            return Error();
        }
        UserMode user = UserUtil.getUser();
        try {
            fileShareService.create(user, data);
        } catch (SQLException e) {
            return Error();
        }
        return OK();
    }

    @PostMapping("Update")
    ReturnMode<Object> UpdateShare(HttpServletResponse response, @RequestBody @Valid UploadShareDTO data) {
        UserMode user = UserUtil.getUser();
        try {
            fileShareService.update(user, data);
        } catch (SQLException | AuthException e) {
            return Error();
        }
        return OK();
    }

    @PostMapping("Delete")
    ReturnMode<Object> DeleteShare(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("id"))
            return Error("参数错误");
        UserMode user = UserUtil.getUser();
        try {
            fileShareService.delete(user, dataMap.get("id"));
        } catch (SQLException | AuthException e) {
            return Error();
        }
        return OK();
    }
}
