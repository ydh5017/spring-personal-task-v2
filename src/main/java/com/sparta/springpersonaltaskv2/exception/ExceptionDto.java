package com.sparta.springpersonaltaskv2.exception;

import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import lombok.Getter;

@Getter
public class ExceptionDto {

    private String result;
    private ErrorCodeType errorCodeType;
    private String message;

    public ExceptionDto(ErrorCodeType errorCodeType) {
        this.result = "ERROR";
        this.errorCodeType = errorCodeType;
        this.message = errorCodeType.getMessage();
    }

    public ExceptionDto(String message) {
        this.result = "ERROR";
        this.message = message;
    }
}
