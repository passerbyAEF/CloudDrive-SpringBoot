package com.clouddrive.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clouddrive.common.filecore.domain.FolderMode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FolderMapper extends BaseMapper<FolderMode> {
}
