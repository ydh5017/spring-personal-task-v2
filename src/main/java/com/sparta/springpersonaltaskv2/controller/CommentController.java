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

    @PostMapping
    public CommentResponseDto addComment(
            @RequestBody @Valid CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.addComment(commentRequestDto, userDetails.getUser());
    }

    @GetMapping
    public List<CommentResponseDto> getComments(Long scheduleId) {
        return commentService.getComments(scheduleId);
    }

    @PutMapping("/{id}")
    public CommentResponseDto updateComment(
            @PathVariable Long id,
            @RequestBody @Valid CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(id, requestDto, userDetails.getUser());
    }

    @DeleteMapping("/{id}")
    public CommentResponseDto deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.deleteComment(id, userDetails.getUser());
    }
}
