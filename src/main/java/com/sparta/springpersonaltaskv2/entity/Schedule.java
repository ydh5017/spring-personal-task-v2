package com.sparta.springpersonaltaskv2.entity;

import com.sparta.springpersonaltaskv2.dto.ScheduleRequestDto;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "schedule")
@NoArgsConstructor
@AllArgsConstructor
public class Schedule extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "schedule", orphanRemoval = true)
    private List<ScheduleFolder> scheduleFolderList = new ArrayList<>();

    @OneToMany(mappedBy = "schedule", orphanRemoval = true)
    private List<File> fileList = new ArrayList<>();

    @OneToMany(mappedBy = "schedule", orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    public Schedule(ScheduleRequestDto scheduleRequestDto, User user) {
        this.title = scheduleRequestDto.getTitle();
        this.content = scheduleRequestDto.getContent();
        this.user = user;
    }

    /**
     * 회원 권한 확인
     * @param user 회원 정보
     */
    public void validate(User user) {
        if (user.getRole() == UserRoleType.USER && !this.user.getUsername().equals(user.getUsername())) {
            throw new ScheduleException(ErrorCodeType.NOT_CREATOR_OR_ADMIN);
        }
    }

    /**
     * 일정 수정
     * @param scheduleRequestDto 일정 요청 DTO
     */
    public void update(ScheduleRequestDto scheduleRequestDto) {
        this.title = scheduleRequestDto.getTitle();
        this.content = scheduleRequestDto.getContent();
    }
}
