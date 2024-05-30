package com.sparta.springpersonaltaskv2.entity;

import com.sparta.springpersonaltaskv2.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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

    /**
     * 댓글 수정
     * @param requestDto 댓글 수정 정보
     */
    public void update(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
