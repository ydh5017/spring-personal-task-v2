package com.sparta.springpersonaltaskv2.aop;

import com.sparta.springpersonaltaskv2.dto.ScheduleRequestDto;
import com.sparta.springpersonaltaskv2.dto.ScheduleResponseDto;
import com.sparta.springpersonaltaskv2.entity.ScheduleHistory;
import com.sparta.springpersonaltaskv2.entity.User;
import com.sparta.springpersonaltaskv2.enums.AspectType;
import com.sparta.springpersonaltaskv2.repository.ScheduleHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j(topic = "LoginHistoryAspect")
public class ScheduleHistoryAspect {

    private final ScheduleHistoryRepository scheduleHistoryRepository;

    @Before(value = "execution(* com.sparta.springpersonaltaskv2.service.ScheduleService.createSchedule(..)) && args(requestDto, user)", argNames = "requestDto,user")
    public void beforeAddSchedule(ScheduleRequestDto requestDto, User user) {
        log.info("[일정 등록 시도] 작성자 : {}, 제목 : {}", user.getUsername(), requestDto.getContent());
    }

    @AfterReturning(value = "execution(* com.sparta.springpersonaltaskv2.service.ScheduleService.createSchedule(..))", returning = "result")
    public void afterAddScheduleSuccess(ScheduleResponseDto result) {
        ScheduleHistory scheduleHistory = new ScheduleHistory(result, AspectType.AFTER_RETURNING);
        scheduleHistoryRepository.save(scheduleHistory);
        log.info("[일정 등록 성공] 작성자 : {}, 일정 ID : {}", result.getWriter(), result.getId());
    }

    @AfterThrowing(value = "execution(* com.sparta.springpersonaltaskv2.service.ScheduleService.createSchedule(..))", throwing = "e")
    public void afterAddScheduleFailure(Throwable e) {
        log.error("[일정 등록 실패] message : {}", e.getMessage());

    }
}
