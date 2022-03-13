package com.thoughtworks.midquiz.midquiz.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResult {

    private String timestamp;
    private String message;
    private int errorCode;
}
