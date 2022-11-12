package com.clouddrive.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.mapper.FolderMapper;
import com.clouddrive.main.service.FileListService;
import com.clouddrive.model.data.FileMode;
import com.clouddrive.model.data.FolderMode;
import com.clouddrive.model.data.UserMode;
import com.clouddrive.model.view.FileViewNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileListServiceImpl implements FileListService {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FolderMapper folderMapper;

    @Override
    public List<FileViewNode> getList(UserMode user, int folderId) {
        List<FileMode> fileList = fileMapper.findFileByFolderIdAndUserId(user.getId(), folderId);
        List<FolderMode> folderList = folderMapper.findFolderByParentIdAndUserId(user.getId(), folderId);
        List<FileViewNode> viewList = new ArrayList<>();
        for (FolderMode item : folderList) {
            //非null代表没有进行删除
            if (item.getDeleteTime() == null)
                viewList.add(new FileViewNode(item));
        }
        for (FileMode item : fileList) {
            if (item.getDeleteTime() == null)
                viewList.add(new FileViewNode(item));
        }
        return viewList;
    }

    @Override
    public FolderMode getRoot(UserMode user) {
        return folderMapper.findRootFolderId(user.getId());
    }
}
