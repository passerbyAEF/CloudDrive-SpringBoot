package com.clouddrive.common.filecore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.Date;

@Data
@NoArgsConstructor
public class CreateShareDTO {
    Integer id;
    Integer type;
    @NonNull
    Boolean isUseCipher;
    @NonNull
    Boolean isUseOverdue;
    String pwd;
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date overdueTime;
}
