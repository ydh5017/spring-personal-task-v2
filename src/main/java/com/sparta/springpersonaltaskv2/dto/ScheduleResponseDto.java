package com.sparta.springpersonaltaskv2.dto;

import com.sparta.springpersonaltaskv2.entity.Schedule;
import com.sparta.springpersonaltaskv2.entity.ScheduleFolder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ScheduleResponseDto {

    private Long id;                    // 번호
    private String title;               // 제목
    private String content;             // 내용
    private String writer;             // 작성자
    private LocalDateTime createdAt;    // 작성일
    private LocalDateTime modifiedAt;   // 수정일

    private List<FolderResponseDto> scheduleFolderList = new ArrayList<>();

    public ScheduleResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.content = schedule.getContent();
        this.writer = schedule.getUser().getUsername();
        this.createdAt = schedule.getCreatedAt();
        this.modifiedAt = schedule.getModifiedAt();
        this.scheduleFolderList = schedule.getScheduleFolderList()
                .stream().map(s->new FolderResponseDto(s.getFolder())).toList();
    }

    @Builder
    public ScheduleResponseDto(Long id, String title, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
