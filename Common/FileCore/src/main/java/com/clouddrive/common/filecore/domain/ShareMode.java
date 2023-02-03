package com.clouddrive.common.filecore.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clouddrive.common.filecore.dto.CreateShareDTO;
import com.clouddrive.common.filecore.dto.UploadShareDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName("share_table")
public class ShareMode {
    public ShareMode(CreateShareDTO dto) {
        this.entityId = dto.getId();
        this.entityType = dto.getType();
        if (dto.getIsUseOverdue())
            this.overdueTime = dto.getOverdueTime();
        else
            this.overdueTime = null;
        if (dto.getIsUseCipher() && StringUtils.hasLength(dto.getPwd()))
            this.secretKey = dto.getPwd();
        else
            this.secretKey = null;
    }

    @JsonIgnore
    public void setData(UploadShareDTO dto) {
        this.id = dto.getId();
        this.entityId = dto.getEntityId();
        this.entityType = dto.getEntityType();
        if (dto.getIsUseOverdue())
            this.overdueTime = dto.getOverdueTime();
        else
            this.overdueTime = null;
        if (dto.getIsUseCipher() && StringUtils.hasLength(dto.getSecretKey())) {
            this.secretKey = dto.getSecretKey();
        } else {
            this.secretKey = null;
        }
    }

    @TableId(type = IdType.AUTO)
    Integer id;
    Integer sharer;
    Integer entityId;
    Integer entityType;
    String secretKey;
    Date overdueTime;
    Date createTime;
    Date updateTime;
    Date deleteTime;
}
