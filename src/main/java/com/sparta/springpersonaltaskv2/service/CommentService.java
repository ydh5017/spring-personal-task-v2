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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ScheduleService scheduleService;

    /**
     * 댓글 등록
     * @param requestDto 댓글 등록 정보
     * @param user 회원 정보
     * @return 댓글 정보
     */
    public CommentResponseDto addComment(CommentRequestDto requestDto, User user) {
        Schedule schedule = scheduleService.getScheduleById(requestDto.getScheduleId());
        Comment comment = new Comment(requestDto, user, schedule);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    /**
     * 댓글 목록 조회
     * @param scheduleId 일정ID
     * @return 댓글 목록
     */
    public List<CommentResponseDto> getComments(Long scheduleId) {
        return commentRepository.findAllByScheduleId(scheduleId).stream().map(CommentResponseDto::new).toList();
    }

    /**
     * 댓글 수정
     * @param id 댓글ID
     * @param requestDto 댓글 수정 정보
     * @param user 회원 정보
     * @return 댓글 정보
     */
    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, User user) {
        Comment comment = getValidatedComment(id, user);
        comment.update(requestDto);
        return new CommentResponseDto(comment);
    }

    /**
     * 뎃글 삭제
     * @param id 댓글ID
     * @param user 회원 정보
     * @return 댓글 정보
     */
    public CommentResponseDto deleteComment(Long id, User user) {
        Comment comment = getValidatedComment(id, user);
        commentRepository.delete(comment);
        return new CommentResponseDto(comment);
    }

    /**
     * 댓글 조회 및 회원 권한 체크
     * @param id 댓글ID
     * @param user 회원정보
     * @return 댓글 Entity
     */
    private Comment getValidatedComment(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                ()-> new ScheduleException(ErrorCodeType.COMMENT_NOT_FOUND));

        if (user.getRole() == UserRoleType.USER && !comment.getUser().getId().equals(user.getId())) {
            throw new ScheduleException(ErrorCodeType.NOT_CREATOR_OR_ADMIN);
        }
        return comment;
    }
}
