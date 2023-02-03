package com.clouddrive.main.service;

import com.clouddrive.common.filecore.dto.CreateShareDTO;
import com.clouddrive.common.filecore.dto.UploadShareDTO;
import com.clouddrive.common.filecore.view.ShareViewNode;
import com.clouddrive.common.security.domain.UserMode;

import java.util.List;

public interface FileShareService {

    List<ShareViewNode> getList(UserMode user);

    void update(UserMode user, UploadShareDTO shareDTO);

    void create(UserMode user, CreateShareDTO shareDTO);

    void delete(UserMode user, Integer id);

}
