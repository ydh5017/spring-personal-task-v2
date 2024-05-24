package com.sparta.springpersonaltaskv2.exception;

import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import lombok.Getter;

/**
 * 일정 예외
 */
@Getter
public class ScheduleException extends RuntimeException {

    private String result;
    private ErrorCodeType errorCode;
    private String message;

    public ScheduleException(ErrorCodeType errorCode) {
        this.result = "ERROR";
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
