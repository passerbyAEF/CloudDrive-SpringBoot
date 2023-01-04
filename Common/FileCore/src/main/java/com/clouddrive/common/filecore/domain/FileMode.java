package com.clouddrive.common.filecore.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file_table")
public class FileMode {
    @TableId(type = IdType.AUTO)
    Integer id;
    String name;
    Integer folderId;
    String hashId;
    Integer userId;
    Long storage;
    Boolean isRecycle;
    Date createTime;
    Date updateTime;
    Date deleteTime;

}
