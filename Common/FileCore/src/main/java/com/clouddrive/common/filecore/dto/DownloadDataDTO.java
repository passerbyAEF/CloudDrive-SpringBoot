package com.clouddrive.common.filecore.dto;

import lombok.Data;

@Data
public class DownloadDataDTO {
    Boolean ready = false;
    String downloadID;
    Long nodeId;
}
