package com.sparta.springpersonaltaskv2.controller;

import com.sparta.springpersonaltaskv2.dto.CommentRequestDto;
import com.sparta.springpersonaltaskv2.dto.CommentResponseDto;
import com.sparta.springpersonaltaskv2.security.UserDetailsImpl;
import com.sparta.springpersonaltaskv2.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록
     * @param commentRequestDto 일정ID, 댓글 내용
     * @param userDetails 회원 정보
     * @return
     */
    @PostMapping
    public CommentResponseDto addComment(
            @RequestBody @Valid CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.addComment(commentRequestDto, userDetails.getUser());
    }

    /**
     * 댓글 목록 조회
     * @param scheduleId 일정ID
     * @return 댓글 목록
     */
    @GetMapping
    public List<CommentResponseDto> getComments(Long scheduleId) {
        return commentService.getComments(scheduleId);
    }

    /**
     * 댓글 수정
     * @param id 댓글ID
     * @param requestDto 댓글 내용
     * @param userDetails 회원 정보
     * @return 댓글 상세 정보
     */
    @PutMapping("/{id}")
    public CommentResponseDto updateComment(
            @PathVariable Long id,
            @RequestBody @Valid CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(id, requestDto, userDetails.getUser());
    }

    /**
     * 댓글 삭제
     * @param id 댓글ID
     * @param userDetails 회원정보
     * @return 댓글 상세 정보
     */
    @DeleteMapping("/{id}")
    public CommentResponseDto deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.deleteComment(id, userDetails.getUser());
    }
}
