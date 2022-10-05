package com.clouddrive.main.controller;

import com.clouddrive.main.service.FileCoreService;
import com.clouddrive.main.service.FileListService;
import com.clouddrive.util.BaseController;
import com.clouddrive.util.ReturnMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ResponseBody
@RequestMapping("File")
public class FileController extends BaseController {

    @Autowired
    FileListService fileListService;
    @Autowired
    FileCoreService fileCoreService;

    @GetMapping("List")
    ReturnMode<Object> List(HttpServletResponse response, @RequestParam Integer folderId) throws IOException {
        if (folderId == null)
            return Error("参数错误");
        return OK(fileListService.getList(getUser(), folderId));
    }

    @PostMapping("Upload")
    ReturnMode<Object> Upload(HttpServletResponse response, @RequestParam String name, @RequestParam Integer folderId, @RequestParam String hash, @RequestParam Long size) throws IOException {
        fileCoreService.Upload(getUser(), name, folderId, hash, size);
        return OK();
    }

    @PostMapping("GetUploadFlag")
    ReturnMode<Object> GetUploadFlag(HttpServletResponse response, @RequestParam String hash, @RequestParam Long size) throws IOException {
        fileCoreService.getUploadFlag(hash, size);
        return OK();
    }

    @PostMapping("Download")
    ReturnMode<Object> Download(HttpServletResponse response, @RequestParam Integer folderId, @RequestParam String hash) throws IOException {

        return OK();
    }

    @PostMapping("MoveFile")
    ReturnMode<Object> MoveFile(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("DeleteFile")
    ReturnMode<Object> DeleteFile(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("RenameFile")
    ReturnMode<Object> RenameFile(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("MoveFolder")
    ReturnMode<Object> MoveFolder(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("DeleteFolder")
    ReturnMode<Object> DeleteFolder(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("RenameFolder")
    ReturnMode<Object> RenameFolder(HttpServletResponse response) throws IOException {

        return OK();
    }
}
