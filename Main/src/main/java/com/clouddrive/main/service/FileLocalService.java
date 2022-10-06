package com.clouddrive.main.service;

import com.clouddrive.model.data.UserMode;

//主要承接不需要与文件中心交换数据的，只涉及到本地数据库修改的操作
public interface FileLocalService {

    boolean linkFileAndHash(Integer user, String name, Integer size, Integer folderId, String hashStr);

    boolean MoveFile(UserMode user, int fileId, int toFolderId);

    boolean DeleteFile(UserMode user, int fileId);

    boolean RenameFile(UserMode user, int fileId, String name);

    boolean CreateFolder(UserMode user, String name, int inFolderId);

    boolean MoveFolder(UserMode user, int folderId, int toFolderId);

    boolean DeleteFolder(UserMode user, int folderId);

    boolean RenameFolder(UserMode user, int folderId, String name);
}
