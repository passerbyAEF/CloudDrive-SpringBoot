package com.clouddrive.common.metadata.constant;

//记录各个环境下，用于计算雪花算法ID的WorkID值
public class WorkIDConstants {
    //存放当前服务分发的ID
    public static Long NodeID;

    //服务注册时，向ID服务器请求ID时使用的workID
    public static final int ServiceID = 0;
    //创建上传任务时使用的workID
    public static final int UploadID = 1;
    //创建下载任务时使用的workID
    public static final int DownloadID = 2;

}
