package com.sparta.springpersonaltaskv2.dto;

import com.sparta.springpersonaltaskv2.entity.Folder;
import lombok.Getter;

@Getter
public class FolderResponseDto {

    private Long id;        // 폴더ID
    private String name;    // 폴더 이름

    public FolderResponseDto(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
    }
}
