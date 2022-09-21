package com.clouddrive.main.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnMode<T> {
    T Data;
    String Message;
}
