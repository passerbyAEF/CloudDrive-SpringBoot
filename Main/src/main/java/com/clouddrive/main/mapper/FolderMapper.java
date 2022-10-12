package com.clouddrive.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clouddrive.model.data.FolderMode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FolderMapper extends BaseMapper<FolderMode> {
    List<FolderMode> findFolderByParentIdAndUserId(int userId, int folderId);

    FolderMode findRootFolderId(int userId);
}
