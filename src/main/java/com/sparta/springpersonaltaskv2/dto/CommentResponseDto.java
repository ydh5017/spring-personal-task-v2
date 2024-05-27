package com.sparta.springpersonaltaskv2.dto;

import com.sparta.springpersonaltaskv2.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CommentResponseDto {

    private Long id;                    // 댓글 ID
    private String content;             // 댓글 내용
    private String userName;            // 작성자
    private String createdAt;           // 작성일
    private String modifiedAt;          // 수정일

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getUser().getUsername();
        this.createdAt = comment.getCreatedAt().toString();
        this.modifiedAt = comment.getModifiedAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
