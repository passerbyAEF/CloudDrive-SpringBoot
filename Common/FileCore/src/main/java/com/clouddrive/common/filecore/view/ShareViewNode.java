package com.clouddrive.common.filecore.view;

import com.clouddrive.common.filecore.domain.ShareMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Date;

@Data
@NoArgsConstructor
public class ShareViewNode {
    public ShareViewNode(ShareMode share) {
        this.sharer = share.getSharer();
        this.id = share.getId();
        this.entityId = share.getEntityId();
        this.entityType = share.getEntityType();
        this.secretKey=share.getSecretKey();
        this.isUseCipher = StringUtils.hasLength(share.getSecretKey());
        this.isUseOverdue = share.getOverdueTime() != null;
        this.overdueTime = share.getOverdueTime();
    }

    String name;
    Integer id;
    Integer sharer;
    Integer entityId;
    String secretKey;
    //1为文件，0为文件夹
    Integer entityType;
    Boolean isUseCipher;
    Boolean isUseOverdue;
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date overdueTime;
}
