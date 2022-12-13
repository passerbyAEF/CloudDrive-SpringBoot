package com.clouddrive.main.service.impl;

import com.clouddrive.common.filecore.domain.FileMode;
import com.clouddrive.common.filecore.domain.FolderMode;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.mapper.FolderMapper;
import com.clouddrive.main.service.FileLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class FileLocalServiceImpl implements FileLocalService {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FolderMapper folderMapper;

    @Override
    public boolean linkFileAndHash(Integer user, String name, Long size, Integer folderId, String hashStr) {
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
        if (file == null || toFolder == null || !file.getUserId().equals(user.getId()) || !toFolder.getOwnerId().equals(user.getId())) {
            return false;
        }
        file.setUpdateTime(new Date());
        file.setFolderId(toFolderId);
        return fileMapper.updateById(file) != 0;
    }

    @Override
    public boolean CopyFile(UserMode user, int fileId, int toFolderId) {
        FileMode file = fileMapper.selectById(fileId);
        FileMode newFile = new FileMode();
        newFile.setName(file.getName());
        newFile.setHashId(file.getHashId());
        newFile.setStorage(file.getStorage());
        newFile.setUserId(user.getId());
        newFile.setFolderId(toFolderId);
        Date now = new Date();
        newFile.setCreateTime(now);
        newFile.setUpdateTime(now);
        return fileMapper.insert(newFile) != 0;
    }

    @Override
    public boolean DeleteFile(UserMode user, int fileId) {
        FileMode file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(user.getId())) {
            return false;
        }
        file.setUpdateTime(new Date());
        file.setDeleteTime(new Date());
        return fileMapper.updateById(file) != 0;
    }

    @Override
    public boolean RenameFile(UserMode user, int fileId, String name) {
        FileMode file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(user.getId())) {
            return false;
        }
        file.setUpdateTime(new Date());
        file.setName(name);
        return fileMapper.updateById(file) != 0;
    }

    @Override
    public int CreateFolder(UserMode user, String name, int inFolderId) {
        if (folderMapper.selectById(inFolderId) == null) {
            return -1;
        }
        FolderMode folder = new FolderMode();
        folder.setName(name);
        folder.setOwnerId(user.getId());
        folder.setParentId(inFolderId);
        Date now = new Date();
        folder.setCreateTime(now);
        folder.setUpdateTime(now);
        if (folderMapper.insert(folder) == 0)
            return -1;
        return folder.getId();
    }

    @Override
    public boolean MoveFolder(UserMode user, int folderId, int toFolderId) {
        FolderMode folder = folderMapper.selectById(folderId);
        FolderMode toFolder = folderMapper.selectById(toFolderId);
        if (folder == null || toFolder == null || !folder.getOwnerId().equals(user.getId()) || !toFolder.getOwnerId().equals(user.getId())) {
            return false;
        }
        folder.setParentId(toFolderId);
        folder.setUpdateTime(new Date());
        return folderMapper.updateById(folder) != 0;
    }

    @Override
    public boolean isChild(int folderId, int findId) {
        if (folderId == findId) return true;
        List<FolderMode> folderList = folderMapper.findFolderByParentId(folderId);
        boolean k = false;
        for (FolderMode item : folderList) {
            if (item.getId() == findId) k = true;
            k = k || isChild(item.getId(), findId);
        }
        return k;
    }

    @Override
    public boolean CopyFolder(UserMode user, int folderId, int toFolderId) {
        FolderMode folder = folderMapper.selectById(folderId);
        FolderMode newFolder = new FolderMode();
        newFolder.setName(folder.getName());
        //newFolder.setParentId(toFolderId);
        newFolder.setOwnerId(user.getId());
        Date now = new Date();
        newFolder.setCreateTime(now);
        newFolder.setUpdateTime(now);
        folderMapper.insert(newFolder);

        List<FileMode> fileList = fileMapper.findFileByFolderIdAndUserId(user.getId(), folderId);
        List<FolderMode> folderList = folderMapper.findFolderByParentIdAndUserId(user.getId(), folderId);
        for (FolderMode item : folderList) {
            copyFolderNode(user, item.getId(), newFolder.getId());
        }
        for (FileMode item : fileList) {
            CopyFile(user, item.getId(), newFolder.getId());
        }

        newFolder.setParentId(toFolderId);
        folderMapper.updateById(newFolder);
        return true;
    }

    private int copyFolderNode(UserMode user, int folderId, int toFolderId) {
        FolderMode folder = folderMapper.selectById(folderId);
        FolderMode newFolder = new FolderMode();
        newFolder.setName(folder.getName());
        newFolder.setParentId(toFolderId);
        newFolder.setOwnerId(user.getId());
        Date now = new Date();
        newFolder.setCreateTime(now);
        newFolder.setUpdateTime(now);
        folderMapper.insert(newFolder);

        List<FileMode> fileList = fileMapper.findFileByFolderIdAndUserId(user.getId(), folderId);
        List<FolderMode> folderList = folderMapper.findFolderByParentIdAndUserId(user.getId(), folderId);
        for (FolderMode item : folderList) {
            copyFolderNode(user, item.getId(), newFolder.getId());
        }
        for (FileMode item : fileList) {
            CopyFile(user, item.getId(), newFolder.getId());
        }
        return newFolder.getId();
    }

    @Override
    public boolean DeleteFolder(UserMode user, int folderId) {
        FolderMode folder = folderMapper.selectById(folderId);
        if (folder == null || !folder.getOwnerId().equals(user.getId())) {
            return false;
        }
        List<FolderMode> folderList = folderMapper.findFolderByParentIdAndUserId(user.getId(), folderId);
        List<FileMode> fileList = fileMapper.findFileByFolderIdAndUserId(user.getId(), folderId);
        for (FolderMode item : folderList) {
            DeleteFolder(user, item.getId());
        }
        for (FileMode item : fileList) {
            DeleteFile(user, item.getId());
        }
        Date now = new Date();
        folder.setUpdateTime(now);
        folder.setDeleteTime(now);
        return folderMapper.updateById(folder) != 0;
    }

    @Override
    public boolean RenameFolder(UserMode user, int folderId, String name) {
        FolderMode folder = folderMapper.selectById(folderId);
        if (folder == null || !folder.getOwnerId().equals(user.getId())) {
            return false;
        }
        folder.setName(name);
        folder.setUpdateTime(new Date());
        return folderMapper.updateById(folder) != 0;
    }
}
