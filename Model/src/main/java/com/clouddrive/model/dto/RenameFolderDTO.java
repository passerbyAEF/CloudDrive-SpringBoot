package com.clouddrive.model.dto;

import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@Data
public class RenameFolderDTO {
    @NonNull
    Integer folderId;
    @NotBlank
    String name;
}
