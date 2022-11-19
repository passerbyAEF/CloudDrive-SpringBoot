package com.clouddrive.main.service;

import com.clouddrive.common.filecore.domain.FolderMode;
import com.clouddrive.common.filecore.view.FileViewNode;
import com.clouddrive.common.security.domain.UserMode;

import java.util.List;

//文件列表搜素
public interface FileListService {
    List<FileViewNode> getList(UserMode user, int folderId);

    FolderMode getRoot(UserMode user);
}
