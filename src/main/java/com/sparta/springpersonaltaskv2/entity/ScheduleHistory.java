package com.sparta.springpersonaltaskv2.entity;

import com.sparta.springpersonaltaskv2.dto.ScheduleResponseDto;
import com.sparta.springpersonaltaskv2.enums.AspectType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ScheduleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long scheduleId;

    @Column
    private String username;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AspectType aspectType;

    @LastModifiedDate
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    public ScheduleHistory(ScheduleResponseDto responseDto, AspectType afterReturning) {
        this.scheduleId = responseDto.getId();
        this.username = responseDto.getWriter();
        this.aspectType = afterReturning;
        this.modifiedAt = responseDto.getModifiedAt();
    }
}
