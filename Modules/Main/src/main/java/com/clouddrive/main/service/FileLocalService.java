package com.clouddrive.main.service;

import com.clouddrive.common.filecore.dto.RecycleDTO;
import com.clouddrive.common.security.domain.UserMode;

import java.util.List;

//主要承接不需要与文件中心交换数据的，只涉及到本地数据库修改的操作
public interface FileLocalService {

    void linkFileAndHash(Integer user, String name, Long size, Integer folderId, String hashStr);

    long GetStorage(Integer userId);

    long GetMaxStorage(Integer userId);

    boolean MoveFile(UserMode user, int fileId, int toFolderId);

    void CopyFile(UserMode user, int fileId, int toFolderId);

    void DeleteFile(UserMode user, int fileId);

    boolean RenameFile(UserMode user, int fileId, String name);

    boolean CreateZeroFile(UserMode user, int folderId, String name);

    void RecoveryRecycle(UserMode user, List<RecycleDTO> data);

    void DeleteRecycle(UserMode user, List<RecycleDTO> data);

    int CreateFolder(UserMode user, String name, int inFolderId);

    boolean MoveFolder(UserMode user, int folderId, int toFolderId);

    void CopyFolder(UserMode user, int folderId, int toFolderId);

    boolean isChild(int folderId, int toFolderId);

    void DeleteFolder(UserMode user, int folderId);

    boolean RenameFolder(UserMode user, int folderId, String name);
}
