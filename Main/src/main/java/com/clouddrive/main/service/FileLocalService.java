package com.clouddrive.main.service;

import com.clouddrive.model.data.UserMode;

//主要承接不需要与文件中心交换数据的，只涉及到本地数据库修改的操作
public interface FileLocalService {

    boolean linkFileAndHash(Integer user, String name, Integer size, Integer folderId, String hashStr);

    void MoveFile(UserMode user, int fileId, int toFolderId);

    void DeleteFile(UserMode user, int fileId);

    void RenameFile(UserMode user, int fileId, String name);

    void CreateFolder(UserMode user, String name, int inFolderId);

    void MoveFolder(UserMode user, int folderId, int toFolderId);

    void DeleteFolder(UserMode user, int fileId);

    void RenameFolder(UserMode user, int fileId, String name);
}
