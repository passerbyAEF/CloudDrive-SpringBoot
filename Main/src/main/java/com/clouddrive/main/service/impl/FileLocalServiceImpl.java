package com.clouddrive.main.service.impl;

import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.mapper.FolderMapper;
import com.clouddrive.main.service.FileLocalService;
import com.clouddrive.model.data.FileMode;
import com.clouddrive.model.data.UserMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FileLocalServiceImpl implements FileLocalService {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FolderMapper folderMapper;

    @Override
    public boolean linkFileAndHash(Integer user, String name, Integer size, Integer folderId, String hashStr) {
        if (user == null || StringUtils.isEmpty(name) || size == null || folderId == null || StringUtils.isEmpty(hashStr)) {
            return false;
        }
        FileMode fileMode = new FileMode();
        fileMode.setUserId(user);
        fileMode.setHashId(hashStr);
        fileMode.setName(name);
        fileMode.setStorage(size);
        fileMode.setFolderId(folderId);
        Date now = new Date();
        fileMode.setCreateTime(now);
        fileMode.setUpdateTime(now);
        fileMapper.insert(fileMode);
        return true;
    }

    @Override
    public void MoveFile(UserMode user, int fileId, int toFolderId) {

    }

    @Override
    public void DeleteFile(UserMode user, int fileId) {

    }

    @Override
    public void RenameFile(UserMode user, int fileId, String name) {

    }

    @Override
    public void CreateFolder(UserMode user, String name, int inFolderId) {

    }

    @Override
    public void MoveFolder(UserMode user, int folderId, int toFolderId) {

    }

    @Override
    public void DeleteFolder(UserMode user, int fileId) {

    }

    @Override
    public void RenameFolder(UserMode user, int fileId, String name) {

    }
}
