package com.clouddrive.main.controller;

import com.clouddrive.main.service.FileCoreService;
import com.clouddrive.main.service.FileListService;
import com.clouddrive.main.service.FileLocalService;
import com.clouddrive.model.data.UserMode;
import com.clouddrive.model.dto.CreateFolderDTO;
import com.clouddrive.model.dto.RenameFileDTO;
import com.clouddrive.model.dto.RenameFolderDTO;
import com.clouddrive.model.dto.UploadDTO;
import com.clouddrive.util.BaseController;
import com.clouddrive.util.ReturnMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("File")
@CrossOrigin
public class FileController extends BaseController {

    @Autowired
    FileListService fileListService;
    @Autowired
    FileCoreService fileCoreService;
    @Autowired
    FileLocalService fileLocalService;

    @GetMapping("List")
    ReturnMode<Object> List(HttpServletResponse response, @RequestParam Integer folderId) {
        UserMode user = getUser();
        return OK(fileListService.getList(getUser(), folderId));
    }

    @GetMapping("GetRoot")
    ReturnMode<Object> GetRoot(HttpServletResponse response) {
        UserMode user = getUser();
        return OK(fileListService.getRoot(getUser()).getId());
    }

    @PostMapping("Upload")
    ReturnMode<Object> Upload(HttpServletResponse response, @RequestBody @Valid UploadDTO uploadDTO) {
        String flag;
        try {
            flag = fileCoreService.Upload(getUser(), uploadDTO.getName(), uploadDTO.getFolderId(), uploadDTO.getHash(), uploadDTO.getSize());
        } catch (IOException e) {
            return Error(e.getMessage());
        }
        return OK(flag);
    }

    @GetMapping("GetUploadFlag")
    ReturnMode<Object> GetUploadFlag(HttpServletResponse response, @RequestParam String flag) {
        return OK(fileCoreService.getUploadFlag(flag));
    }

    @PostMapping("Download")
    ReturnMode<Object> Download(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("fileId"))
            return Error("参数错误");
        String flag;
        try {
            flag = fileCoreService.Download(getUser(), dataMap.get("fileId"));
        } catch (IOException e) {
            return Error(e.getMessage());
        }
        return OK(flag);
    }

    @GetMapping("GetDownloadFlag")
    ReturnMode<Object> GetDownloadFlag(HttpServletResponse response, @RequestParam String flag) {
        return OK(fileCoreService.getDownloadFlag(flag));
    }

    @PostMapping("MoveFile")
    ReturnMode<Object> MoveFile(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("fileId") && !dataMap.containsKey("toFolderId"))
            return Error("参数错误");
        if (!fileLocalService.MoveFile(getUser(), dataMap.get("fileId"), dataMap.get("toFolderId"))) {
            return Error();
        }
        return OK();
    }

    @PostMapping("CopyFile")
    ReturnMode<Object> CopyFile(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("fileId") && !dataMap.containsKey("toFolderId"))
            return Error("参数错误");
        if (!fileLocalService.CopyFile(getUser(), dataMap.get("fileId"), dataMap.get("toFolderId"))) {
            return Error();
        }
        return OK();
    }

    @PostMapping("DeleteFile")
    ReturnMode<Object> DeleteFile(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("fileId"))
            return Error("参数错误");
        if (!fileLocalService.DeleteFile(getUser(), dataMap.get("fileId"))) {
            return Error();
        }
        return OK();
    }

    @PostMapping("RenameFile")
    ReturnMode<Object> RenameFile(HttpServletResponse response, @RequestBody @Valid RenameFileDTO data) {
        if (!fileLocalService.RenameFile(getUser(), data.getFileId(), data.getName())) {
            return Error();
        }
        return OK();
    }

    @PostMapping("CreateFolder")
    ReturnMode<Object> CreateFolder(HttpServletResponse response, @RequestBody @Valid CreateFolderDTO folderDTO) {
        if (!fileLocalService.CreateFolder(getUser(), folderDTO.getName(), folderDTO.getParentId())) {
            return Error();
        }
        return OK();
    }

    @PostMapping("MoveFolder")
    ReturnMode<Object> MoveFolder(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("folderId") && !dataMap.containsKey("toFolderId"))
            return Error("参数错误");
        if (fileLocalService.isChild(dataMap.get("folderId"), dataMap.get("toFolderId"))) {
            return Error("请不要移动到子文件夹内");
        }
        if (!fileLocalService.MoveFolder(getUser(), dataMap.get("folderId"), dataMap.get("toFolderId"))) {
            return Error();
        }
        return OK();
    }

    @PostMapping("CopyFolder")
    ReturnMode<Object> CopyFolder(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("folderId") && !dataMap.containsKey("toFolderId"))
            return Error("参数错误");
        if (!fileLocalService.CopyFolder(getUser(), dataMap.get("folderId"), dataMap.get("toFolderId"))) {
            return Error();
        }
        return OK();
    }

    @PostMapping("DeleteFolder")
    ReturnMode<Object> DeleteFolder(HttpServletResponse response, @RequestBody Map<String, Integer> dataMap) {
        if (!dataMap.containsKey("folderId"))
            return Error("参数错误");
        if (!fileLocalService.DeleteFolder(getUser(), dataMap.get("folderId"))) {
            return Error();
        }
        return OK();
    }

    @PostMapping("RenameFolder")
    ReturnMode<Object> RenameFolder(HttpServletResponse response, @RequestBody @Valid RenameFolderDTO data) {
        if (!fileLocalService.RenameFolder(getUser(), data.getFolderId(), data.getName())) {
            return Error();
        }
        return OK();
    }
}
