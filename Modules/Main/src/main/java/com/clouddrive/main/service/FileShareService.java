package com.clouddrive.main.service;

import com.clouddrive.common.filecore.dto.CreateShareDTO;
import com.clouddrive.common.filecore.dto.UploadShareDTO;
import com.clouddrive.common.filecore.view.FileViewNode;
import com.clouddrive.common.filecore.view.ShareViewNode;
import com.clouddrive.common.security.domain.UserMode;

import java.io.IOException;
import java.util.List;

public interface FileShareService {

    List<ShareViewNode> getList(UserMode user);

    List<FileViewNode> getFileListForShare(Integer shareId,String path);

    Integer getEntityId(Integer shareId);

    boolean hasCipher(Integer shareId);

    void update(UserMode user, UploadShareDTO shareDTO);

    void create(UserMode user, CreateShareDTO shareDTO);

    void delete(UserMode user, Integer id);

    String DownloadShareFile(int shareId,String path,String fileName) throws IOException;

    boolean isOutdated(Integer id);
}
