package com.clouddrive.main.controller;

import com.clouddrive.main.service.FileListService;
import com.clouddrive.util.BaseController;
import com.clouddrive.util.ReturnMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ResponseBody
@RequestMapping("File")
public class FileController extends BaseController {

    @Autowired
    FileListService fileListService;

    @GetMapping("List")
    ReturnMode<Object> List(HttpServletResponse response) throws IOException {
        return OK(fileListService.getList(getUser(), 0));
    }

    @PostMapping("Upload")
    ReturnMode<Object> Upload(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("Download")
    ReturnMode<Object> Download(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("Move")
    ReturnMode<Object> Move(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("Delete")
    ReturnMode<Object> Delete(HttpServletResponse response) throws IOException {

        return OK();
    }

    @PostMapping("Rename")
    ReturnMode<Object> Rename(HttpServletResponse response) throws IOException {

        return OK();
    }
}
