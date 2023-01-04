package com.clouddrive.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clouddrive.common.filecore.domain.FileMode;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<FileMode> {

    List<FileMode> findFileByFolderIdAndUserId(int userId, int folderId);

    List<FileMode> findFileByFolderId(int folderId);

    List<FileMode> findFileByUserId(int userId);

    List<FileMode> findRecycleFileInLastTime(int userId,Date time);
}
