package com.clouddrive.main.service;

import com.clouddrive.common.filecore.view.FileViewNode;
import com.clouddrive.common.security.domain.UserMode;

public interface FileShareService {

    FileViewNode getList (UserMode user);
}
