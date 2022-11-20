package com.clouddrive.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnMode<T> {
    T data;
    String message;
    Integer status;
}
