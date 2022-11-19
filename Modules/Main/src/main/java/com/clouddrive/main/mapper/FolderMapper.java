package com.clouddrive.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clouddrive.common.filecore.domain.FolderMode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FolderMapper extends BaseMapper<FolderMode> {
    List<FolderMode> findFolderByParentIdAndUserId(int userId, int folderId);

    List<FolderMode> findFolderByParentId(int folderId);

    FolderMode findRootFolderId(int userId);
}
