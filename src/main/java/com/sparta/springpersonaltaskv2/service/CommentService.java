package com.sparta.springpersonaltaskv2.service;

import com.sparta.springpersonaltaskv2.dto.CommentRequestDto;
import com.sparta.springpersonaltaskv2.dto.CommentResponseDto;
import com.sparta.springpersonaltaskv2.entity.Comment;
import com.sparta.springpersonaltaskv2.entity.Schedule;
import com.sparta.springpersonaltaskv2.entity.User;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import com.sparta.springpersonaltaskv2.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleService scheduleService;

    public CommentResponseDto addComment(CommentRequestDto requestDto, User user) {
        Schedule schedule = scheduleService.getScheduleById(requestDto.getScheduleId());
        Comment comment = new Comment(requestDto, user, schedule);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    public List<CommentResponseDto> getComments(Long scheduleId) {
        return commentRepository.findAllByScheduleId(scheduleId).stream().map(CommentResponseDto::new).toList();
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, User user) {
        Comment comment = getValidatedComment(id, user);
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    public CommentResponseDto deleteComment(Long id, User user) {
        Comment comment = getValidatedComment(id, user);
        commentRepository.delete(comment);
        return new CommentResponseDto(comment);
    }

    private Comment getValidatedComment(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                ()-> new ScheduleException(ErrorCodeType.COMMENT_NOT_FOUND));

        if (user.getRole() == UserRoleType.USER && !comment.getUser().getId().equals(user.getId())) {
            throw new ScheduleException(ErrorCodeType.NOT_CREATOR_OR_ADMIN);
        }
        return comment;
    }
}
