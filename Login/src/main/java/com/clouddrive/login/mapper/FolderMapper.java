package com.clouddrive.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clouddrive.model.data.FolderMode;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface FolderMapper extends BaseMapper<FolderMode> {
}
