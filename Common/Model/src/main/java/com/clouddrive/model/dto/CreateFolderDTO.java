package com.clouddrive.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CreateFolderDTO {
    @NotBlank
    String name;
    @NonNull
    Integer parentId;
}
