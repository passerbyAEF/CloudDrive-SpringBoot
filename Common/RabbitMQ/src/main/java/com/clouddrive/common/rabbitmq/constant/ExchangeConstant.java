package com.clouddrive.common.rabbitmq.constant;

public class ExchangeConstant {
    //主应用向文件服务广播搜索文件的路由器
    public static final String FindFileExchangeName = "Find.File.Exchange";
    //文件服务向主应用返回搜索结果的路由器
    public static final String ReturnFindFileDataExchangeName = "Return.Find.File.Exchange";
    //文件服务向主应用返回搜索结果的队列
    public static final String ReturnFindFileDataQueueName = "Return.Find.File.Queue";
}
