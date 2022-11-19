package com.clouddrive.common.filecore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UploadDTO {
    @NotBlank
    String name;
    @NonNull
    Integer folderId;
    @NotBlank
    String hash;
    @NonNull
    Long size;
}
