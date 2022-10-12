package com.clouddrive.main.service;

import com.clouddrive.model.data.FolderMode;
import com.clouddrive.model.data.UserMode;
import com.clouddrive.model.view.FileViewNode;

import java.util.List;

//文件列表搜素
public interface FileListService {
    List<FileViewNode> getList(UserMode user,int folderId);

    FolderMode getRoot(UserMode user);
}
