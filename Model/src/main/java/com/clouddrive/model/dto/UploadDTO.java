package com.clouddrive.model.dto;

import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@Data
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
