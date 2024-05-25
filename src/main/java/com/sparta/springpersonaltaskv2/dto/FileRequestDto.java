package com.sparta.springpersonaltaskv2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRequestDto {

    private Long scheduleId; // 일정 번호
    private String fileName; // 파일 이름
    private String saveName; // 저장할 파일 이름
    private Long size;       // 파일 크기

    @Builder
    public FileRequestDto(String fileName, String saveName, Long size) {
        this.fileName = fileName;
        this.saveName = saveName;
        this.size = size;
    }
}
