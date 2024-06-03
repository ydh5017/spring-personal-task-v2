package com.sparta.springpersonaltaskv2.entity;

import com.sparta.springpersonaltaskv2.dto.CommentRequestDto;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    public Comment(CommentRequestDto requestDto, User user, Schedule schedule) {
        this.content = requestDto.getContent();
        this.user = user;
        this.schedule = schedule;
    }

    public void validate(User user) {
        if (user.getRole() == UserRoleType.USER && !this.user.getId().equals(user.getId())) {
            throw new ScheduleException(ErrorCodeType.NOT_CREATOR_OR_ADMIN);
        }
    }

    /**
     * 댓글 수정
     * @param requestDto 댓글 수정 정보
     */
    public void update(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
