package com.sparta.springpersonaltaskv2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 예외처리 http 상태코드, 메시지 enum 클래스
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeType {

    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 일정이 존재하지 않습니다."),
    NOT_IMGFILE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "이미지 파일만 업로드할 수 있습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파일이 존재하지 않습니다."),
    NOT_CREATOR_OR_ADMIN(HttpStatus.FORBIDDEN, "해당 일정의 작성자나 관리자가 아닙니다."),
    DUPLICATED_FOLDER(HttpStatus.LOCKED, "폴더명이 중복되었습니다."),
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 폴더가 존재하지 않습니다."),
    NOT_USER_SCHEDULE_AND_FOLDER(HttpStatus.LOCKED, "사용자의 일정 또는 폴더가 아닙니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
