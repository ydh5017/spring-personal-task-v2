package com.sparta.springpersonaltaskv2.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {

    private Long scheduleId;
    private String content;

    @Builder
    public CommentRequestDto(Long scheduleId, String content) {
        this.scheduleId = scheduleId;
        this.content = content;
    }
}
