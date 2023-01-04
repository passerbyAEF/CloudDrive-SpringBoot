package com.clouddrive.main.service.impl;

import com.clouddrive.common.core.exception.AuthException;
import com.clouddrive.common.core.exception.TransactionalException;
import com.clouddrive.common.filecore.domain.FileMode;
import com.clouddrive.common.filecore.domain.FolderMode;
import com.clouddrive.common.filecore.dto.RecycleDTO;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.mapper.FolderMapper;
import com.clouddrive.main.mapper.UserMapper;
import com.clouddrive.main.service.FileLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class FileLocalServiceImpl implements FileLocalService {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FolderMapper folderMapper;
    @Autowired
    UserMapper userMapper;

    @Transactional
    @Override
    public void linkFileAndHash(Integer userId, String name, Long size, Integer folderId, String hashStr) {
        if (userId == null || StringUtils.isEmpty(name) || size == null || folderId == null || StringUtils.isEmpty(hashStr)) {
            throw new TransactionalException("参数错误");
        }
        FileMode fileMode = new FileMode();
        fileMode.setUserId(userId);
        fileMode.setHashId(hashStr);
        fileMode.setName(name);
        fileMode.setStorage(size);
        fileMode.setFolderId(folderId);
        Date now = new Date();
        fileMode.setCreateTime(now);
        fileMode.setUpdateTime(now);
        if (fileMapper.insert(fileMode) == 0) {
            throw new TransactionalException("SQL错误");
        }
        addStorage(userId, size);
    }

    @Override
    public long GetStorage(Integer userId) {
        UserMode user = userMapper.selectById(userId);
        return user.getStorage();
    }

    @Override
    public long GetMaxStorage(Integer userId) {
        UserMode user = userMapper.selectById(userId);
        return user.getMaxStorage();
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

    @Transactional
    @Override
    public void CopyFile(UserMode user, int fileId, int toFolderId) {
        FileMode file = fileMapper.selectById(fileId);
        if (!file.getUserId().equals(user.getId())) {
            throw new AuthException();
        }
        FileMode newFile = new FileMode();
        newFile.setName(file.getName());
        newFile.setHashId(file.getHashId());
        newFile.setStorage(file.getStorage());
        newFile.setUserId(user.getId());
        newFile.setFolderId(toFolderId);
        Date now = new Date();
        newFile.setCreateTime(now);
        newFile.setUpdateTime(now);
        if (fileMapper.insert(newFile) == 0) {
            throw new TransactionalException("SQL错误");
        }
        addStorage(user.getId(), newFile.getStorage());
    }

    @Transactional
    @Override
    public void DeleteFile(UserMode user, int fileId) {
        FileMode file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(user.getId())) {
            throw new AuthException();
        }
        file.setIsRecycle(true);
        file.setUpdateTime(new Date());
//        file.setDeleteTime(new Date());
        if (fileMapper.updateById(file) == 0) {
            throw new TransactionalException("SQL错误");
        }
        addStorage(user.getId(), -file.getStorage());
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
    public boolean CreateZeroFile(UserMode user, int folderId, String name) {
        FileMode fileMode = new FileMode();
        fileMode.setUserId(user.getId());
        fileMode.setHashId(null);
        fileMode.setName(name);
        fileMode.setStorage(0L);
        fileMode.setFolderId(folderId);
        Date now = new Date();
        fileMode.setCreateTime(now);
        fileMode.setUpdateTime(now);
        return fileMapper.insert(fileMode) != 0;
    }

    @Transactional
    @Override
    public void RecoveryRecycle(UserMode user, List<RecycleDTO> data) {
        for (RecycleDTO item : data) {
            if (item.getIsFile()) {
                FileMode file = fileMapper.selectById(item.getId());
                if (!file.getUserId().equals(user.getId())) {
                    throw new AuthException();
                }
                file.setIsRecycle(false);
                file.setUpdateTime(new Date());
                file.setDeleteTime(null);
                if (fileMapper.updateById(file) == 0) {
                    throw new TransactionalException("SQL错误");
                }
                addStorage(user.getId(), file.getStorage());
            } else {
                FolderMode folder = folderMapper.selectById(item.getId());
                if (!folder.getOwnerId().equals(user.getId())) {
                    throw new AuthException();
                }
                folder.setIsRecycle(false);
                folder.setUpdateTime(new Date());
                folder.setDeleteTime(null);
                if (folderMapper.updateById(folder) == 0) {
                    throw new TransactionalException("SQL错误");
                }
                addStorage(user.getId(), getFolderSize(folder.getId()));
            }
        }
    }

    @Override
    public void DeleteRecycle(UserMode user, List<RecycleDTO> data) {
        for (RecycleDTO item : data) {
            if (item.getIsFile()) {
                FileMode file = fileMapper.selectById(item.getId());
                if (!file.getUserId().equals(user.getId())) {
                    throw new AuthException();
                }
                Date now = new Date();
                file.setUpdateTime(now);
                file.setDeleteTime(now);
                if (fileMapper.updateById(file) == 0) {
                    throw new TransactionalException("SQL错误");
                }
                addStorage(user.getId(), file.getStorage());
            } else {
                FolderMode folder = folderMapper.selectById(item.getId());
                if (!folder.getOwnerId().equals(user.getId())) {
                    throw new AuthException();
                }
                Date now = new Date();
                folder.setUpdateTime(now);
                folder.setDeleteTime(now);
                if (folderMapper.updateById(folder) == 0) {
                    throw new TransactionalException("SQL错误");
                }
                addStorage(user.getId(), getFolderSize(folder.getId()));
            }
        }
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

    @Transactional
    @Override
    public void CopyFolder(UserMode user, int folderId, int toFolderId) {
        FolderMode folder = folderMapper.selectById(folderId);
        FolderMode newFolder = new FolderMode();
        newFolder.setName(folder.getName());
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
        if (folderMapper.updateById(newFolder) == 0) {
            throw new TransactionalException("SQL错误");
        }

        addStorage(user.getId(), getFolderSize(newFolder.getId()));
    }

    @Transactional
    @Override
    public void DeleteFolder(UserMode user, int folderId) {
        FolderMode folder = folderMapper.selectById(folderId);
        if (folder == null || !folder.getOwnerId().equals(user.getId())) {
            throw new AuthException();
        }
        folder.setIsRecycle(true);
        Date now = new Date();
        folder.setUpdateTime(now);
        if (folderMapper.updateById(folder) == 0) {
            throw new TransactionalException("SQL错误");
        }
        addStorage(user.getId(), -getFolderSize(folderId));
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

    private long getFolderSize(int folderId) {
        List<FolderMode> folderList = folderMapper.findFolderByParentId(folderId);
        List<FileMode> fileList = fileMapper.findFileByFolderId(folderId);
        long size = 0;
        for (FolderMode item : folderList) {
            size += getFolderSize(item.getId());
        }
        for (FileMode item : fileList) {
            size += item.getStorage();
        }
        return size;
    }

    private void addStorage(int userId, long size) {
        UserMode user = userMapper.selectById(userId);
        user.setStorage(user.getStorage() + size);
        if (userMapper.updateById(user) == 0) {
            throw new TransactionalException("SQL错误");
        }
    }
}
