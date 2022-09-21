package com.clouddrive.model.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("folder_table")
public class FolderMode {
    @TableId(type = IdType.AUTO)
    int id;
    String name;
    int ownerId;
    int parentId;
    Date createTime;
    Date updateTime;
    Date deleteTime;
}
