package com.sparta.springpersonaltaskv2.dto;

import com.sparta.springpersonaltaskv2.entity.Folder;
import lombok.Getter;

@Getter
public class FolderResponseDto {

    private Long id;
    private String name;

    public FolderResponseDto(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
    }
}
