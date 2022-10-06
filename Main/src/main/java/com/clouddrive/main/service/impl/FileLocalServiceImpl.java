package com.clouddrive.main.service.impl;

import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.mapper.FolderMapper;
import com.clouddrive.main.service.FileLocalService;
import com.clouddrive.model.data.FileMode;
import com.clouddrive.model.data.FolderMode;
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
        return fileMapper.insert(fileMode) != 0;
    }

    @Override
    public boolean MoveFile(UserMode user, int fileId, int toFolderId) {
        FileMode file = fileMapper.selectById(fileId);
        FolderMode toFolder = folderMapper.selectById(toFolderId);
        if (file == null || toFolder == null || file.getUserId() != user.getId() || toFolder.getOwnerId() != user.getId()) {
            return false;
        }
        file.setUpdateTime(new Date());
        file.setFolderId(toFolderId);
        return fileMapper.updateById(file) != 0;
    }

    @Override
    public boolean DeleteFile(UserMode user, int fileId) {
        FileMode file = fileMapper.selectById(fileId);
        if (file == null || file.getUserId() != user.getId()) {
            return false;
        }
        file.setUpdateTime(new Date());
        file.setDeleteTime(new Date());
        return fileMapper.updateById(file) != 0;
    }

    @Override
    public boolean RenameFile(UserMode user, int fileId, String name) {
        FileMode file = fileMapper.selectById(fileId);
        if (file == null || file.getUserId() != user.getId()) {
            return false;
        }
        file.setUpdateTime(new Date());
        file.setName(name);
        return fileMapper.updateById(file) != 0;
    }

    @Override
    public boolean CreateFolder(UserMode user, String name, int inFolderId) {
        if (folderMapper.selectById(inFolderId) == null) {
            return false;
        }
        FolderMode folder = new FolderMode();
        folder.setName(name);
        folder.setOwnerId(user.getId());
        folder.setParentId(inFolderId);
        Date now = new Date();
        folder.setCreateTime(now);
        folder.setUpdateTime(now);
        return folderMapper.insert(folder) != 0;
    }

    @Override
    public boolean MoveFolder(UserMode user, int folderId, int toFolderId) {
        FolderMode folder = folderMapper.selectById(folderId);
        FolderMode toFolder = folderMapper.selectById(toFolderId);
        if (folder == null || toFolder == null || folder.getOwnerId() != user.getId() || toFolder.getOwnerId() != user.getId()) {
            return false;
        }
        folder.setParentId(toFolderId);
        folder.setUpdateTime(new Date());
        return folderMapper.updateById(folder) != 0;
    }

    @Override
    public boolean DeleteFolder(UserMode user, int folderId) {
        FolderMode folder = folderMapper.selectById(folderId);
        if (folder == null || folder.getOwnerId() != user.getId()) {
            return false;
        }
        Date now = new Date();
        folder.setUpdateTime(now);
        folder.setDeleteTime(now);
        return folderMapper.updateById(folder) != 0;
    }

    @Override
    public boolean RenameFolder(UserMode user, int folderId, String name) {
        FolderMode folder = folderMapper.selectById(folderId);
        if (folder == null || folder.getOwnerId() != user.getId()) {
            return false;
        }
        folder.setName(name);
        folder.setUpdateTime(new Date());
        return folderMapper.updateById(folder) != 0;
    }
}
