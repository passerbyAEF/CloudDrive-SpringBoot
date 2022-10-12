package com.clouddrive.model.view;

import com.clouddrive.model.data.FileMode;
import com.clouddrive.model.data.FolderMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

//用于表示前端显示的文件列表里的每一列
@Data
public class FileViewNode {
    public FileViewNode(FileMode file) {
        this.name = file.getName();
        this.id = file.getId();
        this.type = 1;
        this.storage = file.getStorage();
        this.time = file.getUpdateTime();
    }

    public FileViewNode(FolderMode folder) {
        this.name = folder.getName();
        this.id = folder.getId();
        this.type = 0;
        this.storage = 0;
        this.time = folder.getUpdateTime();
    }

    int id;
    String name;
    //1为文件，0为文件夹
    int type;
    //大小，若是文件夹则为0
    long storage;
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    Date time;
}
