package com.sparta.springpersonaltaskv2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {

    private Long scheduleId;                    // 일정ID

    @NotBlank(message = "댓글 내용을 작성해주세요")
    private String content;                     // 댓글 내용

    @Builder
    public CommentRequestDto(Long scheduleId, String content) {
        this.scheduleId = scheduleId;
        this.content = content;
    }
}
