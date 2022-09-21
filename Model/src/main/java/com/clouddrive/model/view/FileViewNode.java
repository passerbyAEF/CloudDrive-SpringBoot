package com.clouddrive.model.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

//用于表示前端显示的文件列表里的每一列
@Data
public class FileViewNode {
    int name;
    int id;
    //1为文件，0为文件夹
    int type;
    //大小，若是文件夹则为0
    long storage;
    //更新时间
    int time;
}
