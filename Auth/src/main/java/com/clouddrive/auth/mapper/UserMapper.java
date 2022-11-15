package com.clouddrive.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clouddrive.common.security.domain.UserMode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserMode> {
}
