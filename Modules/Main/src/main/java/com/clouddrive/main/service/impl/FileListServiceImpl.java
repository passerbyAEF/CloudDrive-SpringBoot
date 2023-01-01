package com.clouddrive.main.service.impl;

import com.clouddrive.common.core.constant.ScreeConstants;
import com.clouddrive.common.filecore.domain.FileMode;
import com.clouddrive.common.filecore.domain.FolderMode;
import com.clouddrive.common.filecore.view.FileViewNode;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.mapper.FolderMapper;
import com.clouddrive.main.service.FileListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
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
            viewList.add(new FileViewNode(item));
        }
        for (FileMode item : fileList) {
            viewList.add(new FileViewNode(item));
        }
        return viewList;
    }

    //最好是用缓存来优化，但是没时间写，直接力大砖飞
    @Override
    public List<FileViewNode> getScreeList(UserMode user, int flag) {
        List<FileMode> fileList = fileMapper.findFileByUserId(user.getId());
        List<FileViewNode> viewList = new ArrayList<>();
        String i = "[\\s\\S]*";
        switch (flag) {
            case ScreeConstants.IMG_FLAG:
                i += "\\.(bmp|jpg|jpeg|png|gif)$";
                break;
            case ScreeConstants.TEXT_FLAG:
                i += "\\.(ppt|pptx|doc|docx|xls|xlsx|pdf|txt)$";
                break;
            case ScreeConstants.VIDEO_FLAG:
                i += "\\.(mp4|avi|wmv|mpg|mpeg|mov|rm|ram|swf|flv)$";
                break;
            case ScreeConstants.AUDIO_FLAG:
                i += "\\.(mp3|wav|aif|aiff|au|ra|mid|rmi|mkv)$";
                break;
            case ScreeConstants.OUTER_FLAG:
            default:
                //把上面的都包含
                i += "\\.(bmp|jpg|jpeg|png|gif|ppt|pptx|doc|docx|xls|xlsx|pdf|txt|mp4|avi|wmv|mpg|mpeg|mov|rm|ram|swf|flv|mp3|wav|aif|aiff|au|ra|mid|rmi|mkv)$";
                for (FileMode file : fileList) {
                    if (!file.getName().toLowerCase().matches(i)) {
                        viewList.add(new FileViewNode(file));
                    }
                }
                return viewList;
        }
        for (FileMode file : fileList) {
            if (file.getName().toLowerCase().matches(i)) {
                viewList.add(new FileViewNode(file));
            }
        }
        return viewList;
    }

    @Override
    public List<FileViewNode> getRecycleList(UserMode user) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_YEAR, -30);
        List<FileMode> fileList = fileMapper.findDeleteFileInLastTime(user.getId(), ca.getTime());
        List<FolderMode> folderList = folderMapper.findDeleteFolderInLastTime(user.getId(), ca.getTime());
        List<FileViewNode> viewList = new ArrayList<>();
        for (FileMode item : fileList) {
            viewList.add(new FileViewNode(item));
        }
        for (FolderMode item : folderList) {
            viewList.add(new FileViewNode(item));
        }
        return viewList;
    }

    @Override
    public FolderMode getRoot(UserMode user) {
        return folderMapper.findRootFolderId(user.getId());
    }
}
