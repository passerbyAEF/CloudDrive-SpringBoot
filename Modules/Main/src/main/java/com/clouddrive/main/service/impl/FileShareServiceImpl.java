package com.clouddrive.main.service.impl;

import com.clouddrive.common.core.exception.AuthException;
import com.clouddrive.common.core.exception.SQLException;
import com.clouddrive.common.filecore.domain.ShareMode;
import com.clouddrive.common.filecore.dto.CreateShareDTO;
import com.clouddrive.common.filecore.dto.UploadShareDTO;
import com.clouddrive.common.filecore.view.ShareViewNode;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.main.mapper.FileMapper;
import com.clouddrive.main.mapper.FolderMapper;
import com.clouddrive.main.mapper.ShareMapper;
import com.clouddrive.main.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileShareServiceImpl implements FileShareService {

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
}
