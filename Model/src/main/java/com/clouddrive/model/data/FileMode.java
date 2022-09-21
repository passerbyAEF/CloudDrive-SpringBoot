package com.clouddrive.model.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file_table")
public class FileMode {
    @TableId(type = IdType.AUTO)
    int id;
    String name;
    int folderId;
    int hashId;
    long storage;
    Date createTime;
    Date updateTime;
    Date deleteTime;

}
