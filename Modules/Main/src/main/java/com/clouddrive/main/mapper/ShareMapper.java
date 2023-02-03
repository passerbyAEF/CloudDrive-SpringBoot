package com.clouddrive.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clouddrive.common.filecore.domain.ShareMode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShareMapper extends BaseMapper<ShareMode> {

    List<ShareMode> findShareByUserID(int userId);
}
