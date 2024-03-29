package com.clouddrive.main.service.impl;

import com.clouddrive.common.core.exception.AuthException;
import com.clouddrive.common.core.exception.SQLException;
import com.clouddrive.common.filecore.domain.FileMode;
import com.clouddrive.common.filecore.domain.FolderMode;
import com.clouddrive.common.filecore.domain.ShareMode;
import com.clouddrive.common.filecore.dto.CreateShareDTO;
import com.clouddrive.common.filecore.dto.UploadShareDTO;
import com.clouddrive.common.filecore.view.FileViewNode;
import com.clouddrive.common.filecore.view.ShareViewNode;
import com.clouddrive.common.rabbitmq.constant.ExchangeConstant;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.mapper.FolderMapper;
import com.clouddrive.main.mapper.ShareMapper;
import com.clouddrive.main.service.FileLocalService;
import com.clouddrive.main.service.FileShareService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class FileShareServiceImpl implements FileShareService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ShareMapper shareMapper;
    @Autowired
    FileMapper fileMapper;
    @Autowired
    FolderMapper folderMapper;

    @Override
    public List<ShareViewNode> getList(UserMode user) {
        List<ShareMode> shareList = shareMapper.findShareByUserID(user.getId());
        List<ShareViewNode> list = new ArrayList<>();
        for (ShareMode item : shareList) {
            ShareViewNode viewNode = new ShareViewNode(item);
            if (item.getEntityType() == 0) {
                viewNode.setName(folderMapper.selectById(item.getEntityId()).getName());

            } else {
                viewNode.setName(fileMapper.selectById(item.getEntityId()).getName());
            }
            list.add(viewNode);
        }
        return list;
    }

    @Override
    public List<FileViewNode> getFileListForShare(Integer shareId, String path) {
        ShareMode share = shareMapper.selectById(shareId);
        List<FileViewNode> list = new ArrayList<>();
        if (share == null) {
            return list;
        }
        if (share.getEntityType() == 0) {
            FolderMode folder = folderMapper.selectById(share.getEntityId());
            Queue<String> qu = new LinkedList<>();
            if (path.length() > 0 && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            if (path.length() > 0 && path.charAt(path.length() - 1) == '/') {
                path.substring(0, path.length() - 1);
            }
            for (String s : path.split("/")) {
                qu.offer(s);
            }
            FolderMode targetFolder = toThePath(folder, qu);

            List<FolderMode> targetFolderList = folderMapper.findFolderByParentId(targetFolder.getId());
            List<FileMode> targetFileList = fileMapper.findFileByFolderId(targetFolder.getId());
            for (FolderMode item : targetFolderList) {
                list.add(new FileViewNode(item));
            }
            for (FileMode item : targetFileList) {
                list.add(new FileViewNode(item));
            }
        } else {
            FileMode file = fileMapper.selectById(share.getEntityId());
            list.add(new FileViewNode(file));
        }
        return list;
    }

    @Override
    public Integer getEntityId(Integer shareId) {
        return shareMapper.selectById(shareId).getEntityId();
    }

    @Override
    public boolean hasCipher(Integer shareId) {
        return StringUtils.hasLength(shareMapper.selectById(shareId).getSecretKey());
    }

    FolderMode toThePath(FolderMode folder, Queue<String> path) {
        if (!path.isEmpty()) {
            String p = path.poll();
            List<FolderMode> list = folderMapper.findFolderByParentId(folder.getId());
            for (FolderMode f : list) {
                if (f.getName().equals(p)) {
                    return toThePath(f, path);
                }
            }
        }
        return folder;
    }

    @Override
    public void update(UserMode user, UploadShareDTO shareDTO) {
        ShareMode mode = shareMapper.selectById(shareDTO.getId());
        if (!user.getId().equals(mode.getSharer())) {
            throw new AuthException("权限错误");
        }
        mode.setData(shareDTO);
        mode.setUpdateTime(new Date());
        if (shareMapper.updateById(mode) == 0) {
            throw new SQLException("SQL执行错误");
        }
    }

    @Override
    public void create(UserMode user, CreateShareDTO shareDTO) {
        ShareMode mode = new ShareMode(shareDTO);
        mode.setSharer(user.getId());
        Date now = new Date();
        mode.setCreateTime(now);
        mode.setUpdateTime(now);
        if (shareMapper.insert(mode) == 0) {
            throw new SQLException("SQL执行错误");
        }
    }

    @Override
    public void delete(UserMode user, Integer id) {
        ShareMode shareMode = shareMapper.selectById(id);
        if (!user.getId().equals(shareMode.getSharer())) {
            throw new AuthException("权限错误");
        }
        shareMode.setDeleteTime(new Date());
        if (shareMapper.updateById(shareMode) == 0) {
            throw new SQLException("SQL执行错误");
        }
    }

    @Override
    public String DownloadShareFile(int shareId, String path, String fileName) throws IOException {
        ShareMode share = shareMapper.selectById(shareId);
        FileMode fileMode = null;
        if (share.getEntityType() == 0) {
            //提取目标路径的文件夹id
            FolderMode folder = folderMapper.selectById(share.getEntityId());
            Queue<String> qu = new LinkedList<>();
            if (path.length() > 0 && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            if (path.length() > 0 && path.charAt(path.length() - 1) == '/') {
                path.substring(0, path.length() - 1);
            }
            for (String s : path.split("/")) {
                qu.offer(s);
            }
            FolderMode targetFolder = toThePath(folder, qu);

            List<FileMode> fileList = fileMapper.findFileByFolderId(targetFolder.getId());
            for (FileMode item : fileList) {
                if (item.getName().equals(fileName)) {
                    fileMode = item;
                    break;
                }
            }
        } else {
            fileMode = fileMapper.selectById(share.getEntityId());
        }

        if (fileMode == null)
            throw new IOException("文件不存在");
        String flag = fileMode.getHashId();
        Map<String, String> data = new HashMap<>();
        data.put("hash", fileMode.getHashId());
        rabbitTemplate.convertAndSend(ExchangeConstant.FindFileExchangeName, ExchangeConstant.ReturnFindFileDataQueueName, data);
        return flag;
    }

    @Override
    public boolean isOutdated(Integer id) {
        ShareMode mode = shareMapper.selectById(id);
        Date now =new Date();
        return (mode.getDeleteTime()!=null&&mode.getDeleteTime().before(now)) || (mode.getOverdueTime()!=null&&mode.getOverdueTime().before(now));
    }
}
