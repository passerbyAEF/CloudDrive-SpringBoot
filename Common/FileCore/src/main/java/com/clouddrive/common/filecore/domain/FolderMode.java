package com.clouddrive.common.filecore.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("folder_table")
public class FolderMode {
    @TableId(type = IdType.AUTO)
    Integer id;
    String name;
    Integer ownerId;
    Integer parentId;
    Date createTime;
    Date updateTime;
    Date deleteTime;
}
