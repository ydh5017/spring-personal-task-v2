package com.sparta.springpersonaltaskv2.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FolderRequestDto {
    List<String> folderNames; // 폴더 이름 목록
}
