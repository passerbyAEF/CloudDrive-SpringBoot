package com.clouddrive.main.service;

import com.clouddrive.model.data.UserMode;
import com.clouddrive.model.view.FileViewNode;

import java.util.List;

public interface FileListService {
    List<FileViewNode> getList(UserMode user,int folderId);
    void Upload(UserMode user,int folderId);
    void Download(UserMode user,int fileId);
}
