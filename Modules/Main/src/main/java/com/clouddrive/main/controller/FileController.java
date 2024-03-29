package com.clouddrive.main.controller;

import com.clouddrive.common.core.constant.ScreeConstants;
import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.core.exception.AuthException;
import com.clouddrive.common.core.exception.SQLException;
import com.clouddrive.common.filecore.dto.CreateFolderDTO;
import com.clouddrive.common.filecore.dto.RenameFileDTO;
import com.clouddrive.common.filecore.dto.RenameFolderDTO;
import com.clouddrive.common.filecore.dto.UploadDTO;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.common.security.util.UserUtil;
import com.clouddrive.main.service.FileCoreService;
import com.clouddrive.main.service.FileListService;
import com.clouddrive.main.service.FileLocalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/External/File")
public class FileController extends BaseController {

    @Autowired
    FileListService fileListService;
    @Autowired
    FileCoreService fileCoreService;
    @Autowired
    FileLocalService fileLocalService;

    @GetMapping("List")
    ReturnMode<Object> List(HttpServletResponse response, @RequestParam Integer folderId) {
        UserMode user = UserUtil.getUser();
        return OK(fileListService.getList(user, folderId));
    }

    @GetMapping("GetStorage")
    ReturnMode<Object> GetStorage(HttpServletResponse response) {
        UserMode user = UserUtil.getUser();
        return OK(fileLocalService.GetStorage(user.getId()));
    }

    @GetMapping("GetMaxStorage")
    ReturnMode<Object> GetMaxStorage(HttpServletResponse response) {
        UserMode user = UserUtil.getUser();
        return OK(fileLocalService.GetMaxStorage(user.getId()));
    }

    @GetMapping("ScreeList")
    ReturnMode<Object> GetScreeList(HttpServletResponse response, @RequestParam(required = false) Integer flag) {
        UserMode user = UserUtil.getUser();
        if (flag == null)
            flag = ScreeConstants.OUTER_FLAG;
        return OK(fileListService.getScreeList(user, flag));
    }

    @GetMapping("GetRoot")
    ReturnMode<Object> GetRoot(HttpServletResponse response) {
        UserMode user = UserUtil.getUser();
        return OK(fileListService.getRoot(user).getId());
    }

    @PostMapping("Upload")
    ReturnMode<Object> Upload(HttpServletResponse response, @RequestBody @Valid UploadDTO uploadDTO) {
        Map<String, String> data;
        if (uploadDTO.getSize() == 0 && fileLocalService.CreateZeroFile(UserUtil.getUser(), uploadDTO.getFolderId(), uploadDTO.getName())) {
            return OK();
        }
        try {
            data = fileCoreService.Upload(UserUtil.getUser(), uploadDTO.getName(), uploadDTO.getFolderId(), uploadDTO.getHash(), uploadDTO.getSize());
        } catch (IOException e) {
            return Error();
        }
        return OK(data);
    }

    @GetMapping("Download")
    ReturnMode<Object> Download(HttpServletResponse response, @RequestParam Integer fileId) {
        String flag;
        try {
            flag = fileCoreService.Download(UserUtil.getUser(), fileId);
        } catch (IOException e) {
            return Error(e.getMessage());
        }
        return OK(flag);
    }

    @GetMapping("GetDownloadFlag")
    ReturnMode<Object> GetDownloadFlag(HttpServletResponse response, @RequestParam String flag) throws JsonProcessingException {
        return OK(fileCoreService.getDownloadFlag(flag));
    }

    @PostMapping("MoveFile")
    ReturnMode<Object> MoveFile(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("fileId") && !dataMap.containsKey("toFolderId"))
            return Error("参数错误");
        if (!fileLocalService.MoveFile(UserUtil.getUser(), dataMap.get("fileId"), dataMap.get("toFolderId"))) {
            return Error();
        }
        return OK();
    }

    @PostMapping("CopyFile")
    ReturnMode<Object> CopyFile(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("fileId") && !dataMap.containsKey("toFolderId"))
            return Error("参数错误");
        try {
            fileLocalService.CopyFile(UserUtil.getUser(), dataMap.get("fileId"), dataMap.get("toFolderId"));
        } catch (SQLException e) {
            return Error();
        } catch (AuthException e) {
            return Error(e.getMessage());
        }
        return OK();
    }

    @PostMapping("DeleteFile")
    ReturnMode<Object> DeleteFile(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("fileId"))
            return Error("参数错误");
        try {
            fileLocalService.DeleteFile(UserUtil.getUser(), dataMap.get("fileId"));
        } catch (SQLException e) {
            return Error();
        } catch (AuthException e) {
            return Error(e.getMessage());
        }
        return OK();
    }

    @PostMapping("RenameFile")
    ReturnMode<Object> RenameFile(HttpServletResponse response, @RequestBody @Valid RenameFileDTO data) {
        if (!fileLocalService.RenameFile(UserUtil.getUser(), data.getFileId(), data.getName())) {
            return Error();
        }
        return OK();
    }

    @PostMapping("CreateFolder")
    ReturnMode<Object> CreateFolder(HttpServletResponse response, @RequestBody @Valid CreateFolderDTO folderDTO) {
        int newFolderId = fileLocalService.CreateFolder(UserUtil.getUser(), folderDTO.getName(), folderDTO.getParentId());
        if (newFolderId == -1) {
            return Error();
        }
        return OK(newFolderId);
    }

    @PostMapping("MoveFolder")
    ReturnMode<Object> MoveFolder(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("folderId") && !dataMap.containsKey("toFolderId"))
            return Error("参数错误");
        if (fileLocalService.isChild(dataMap.get("folderId"), dataMap.get("toFolderId"))) {
            return Error("请不要移动到子文件夹内");
        }
        if (!fileLocalService.MoveFolder(UserUtil.getUser(), dataMap.get("folderId"), dataMap.get("toFolderId"))) {
            return Error();
        }
        return OK();
    }

    @PostMapping("CopyFolder")
    ReturnMode<Object> CopyFolder(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("folderId") && !dataMap.containsKey("toFolderId"))
            return Error("参数错误");
        try {
            fileLocalService.CopyFolder(UserUtil.getUser(), dataMap.get("folderId"), dataMap.get("toFolderId"));
        } catch (SQLException e) {
            return Error();
        } catch (AuthException e) {
            return Error(e.getMessage());
        }
        return OK();
    }

    @PostMapping("DeleteFolder")
    ReturnMode<Object> DeleteFolder(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("folderId"))
            return Error("参数错误");
        try {
            fileLocalService.DeleteFolder(UserUtil.getUser(), dataMap.get("folderId"));
        } catch (SQLException e) {
            return Error();
        } catch (AuthException e) {
            return Error(e.getMessage());
        }
        return OK();
    }

    @PostMapping("RenameFolder")
    ReturnMode<Object> RenameFolder(HttpServletResponse response, @RequestBody @Valid RenameFolderDTO data) {
        if (!fileLocalService.RenameFolder(UserUtil.getUser(), data.getFolderId(), data.getName())) {
            return Error();
        }
        return OK();
    }
}
