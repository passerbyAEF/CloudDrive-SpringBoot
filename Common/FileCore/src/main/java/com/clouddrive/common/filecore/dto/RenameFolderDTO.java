package com.clouddrive.common.filecore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class RenameFolderDTO {
    @NonNull
    Integer folderId;
    @NotBlank
    String name;
}
