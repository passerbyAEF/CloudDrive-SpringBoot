package com.clouddrive.main.service.impl;

import com.clouddrive.main.service.FileListService;
import com.clouddrive.model.data.UserMode;
import com.clouddrive.model.view.FileViewNode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileListServiceImpl implements FileListService {

    @Override
    public List<FileViewNode> getList(UserMode user, int folderId) {

        return null;
    }

    @Override
    public void Upload(UserMode user, int folderId) {

    }

    @Override
    public void Download(UserMode user, int fileId) {

    }
}
