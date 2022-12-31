package com.clouddrive.main.service.impl;

import com.clouddrive.common.filecore.domain.ShareMode;
import com.clouddrive.common.filecore.view.FileViewNode;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.main.mapper.ShareMapper;
import com.clouddrive.main.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileShareServiceImpl implements FileShareService {

    @Autowired
    ShareMapper shareMapper;

    @Override
    public FileViewNode getList(UserMode user) {
        List<ShareMode> shareList = shareMapper.findShareByUserID(user.getId());

        return null;
    }
}
