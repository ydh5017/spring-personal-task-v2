package com.sparta.springpersonaltaskv2.aop;

import com.sparta.springpersonaltaskv2.dto.ScheduleRequestDto;
import com.sparta.springpersonaltaskv2.dto.ScheduleResponseDto;
import com.sparta.springpersonaltaskv2.entity.ScheduleHistory;
import com.sparta.springpersonaltaskv2.entity.User;
import com.sparta.springpersonaltaskv2.enums.AspectType;
import com.sparta.springpersonaltaskv2.repository.ScheduleHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 일정 관련 로깅 AOP 구성
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j(topic = "ScheduleHistoryAspect")
public class ScheduleHistoryAspect {

    private final ScheduleHistoryRepository scheduleHistoryRepository;

    /**
     * Before : 일정 등록 시도 전 로깅 AOP
     * <p>
     *     메서드가 실행되기 전에 Advice를 실행한다.
     * </p>
     * @param requestDto 일정 정보
     * @param user 회원 정보
     */
    @Before(value = "execution(* com.sparta.springpersonaltaskv2.service.ScheduleService.createSchedule(..)) && args(requestDto, user)", argNames = "requestDto,user")
    public void beforeAddSchedule(ScheduleRequestDto requestDto, User user) {
        log.info("[일정 등록 시도] 작성자 : {}, 제목 : {}", user.getUsername(), requestDto.getContent());
    }

    /**
     * After : 일정 등록 메서드 실행 후 로깅 AOP
     * <p>
     *     메서드가 실행되고 Advice를 실행한다.
     * </p>
     * @param joinPoint Aspect가 적용되는 시점
     * @param user 회원 정보
     * @param requestDto 일정 정보
     */
    @After(value = "execution(* com.sparta.springpersonaltaskv2.service.ScheduleService.createSchedule(..)) && args(requestDto, user)", argNames = "joinPoint,user,requestDto")
    public void afterAddSchedule(JoinPoint joinPoint, User user, ScheduleRequestDto requestDto) {
        log.info("[일정 등록 후] 경로 {}, 작성자 : {}, 제목 : {}",
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                requestDto.getTitle(),
                user.getUsername());
    }

    /**
     * AfterReturning : 일정 등록 성공 로깅 AOP
     * <p>
     *      메서드가 정상적으로 실행되고 반환된 후에 Advice를 실행한다.
     * </p>
     * @param responseDto 일정 정보
     */
    @AfterReturning(value = "execution(* com.sparta.springpersonaltaskv2.service.ScheduleService.createSchedule(..))", returning = "responseDto")
    public void afterAddScheduleSuccess(ScheduleResponseDto responseDto) {
        ScheduleHistory scheduleHistory = new ScheduleHistory(responseDto, AspectType.AFTER_RETURNING);
        scheduleHistoryRepository.save(scheduleHistory);
        log.info("[일정 등록 성공] 작성자 : {}, 일정 ID : {}", responseDto.getWriter(), responseDto.getId());
    }

    /**
     * AfterThrowing : 일정 등록 실패 로깅 AOP
     * <p>
     *     메서드에서 예외 발생 시 Advice를 실행한다.
     * </p>
     * @param e 예외
     */
    @AfterThrowing(value = "execution(* com.sparta.springpersonaltaskv2.service.ScheduleService.createSchedule(..))", throwing = "e")
    public void afterAddScheduleFailure(Throwable e) {
        log.error("[일정 등록 실패] message : {}", e.getMessage());

    }

    /**
     * Around : 일정 등록 전후 로깅 AOP
     * <p>
     *     메서 실핼 전후로 또는 예외 발생 시 Advice를 실행한다.
     * </p>
     * @param joinPoint Aspect가 적용되는 시점
     * @return 메소드 반환 값
     * @throws Throwable 예외
     */
    @Around(value = "execution(* com.sparta.springpersonaltaskv2.service.ScheduleService.createSchedule(..))")
    public Object aroundAddSchedule(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around before: " + joinPoint.getSignature().getName());
        // joinPoint.proceed()를 기준으로 메서드 실행 전후로 나뉨
        ScheduleResponseDto proceed = (ScheduleResponseDto) joinPoint.proceed();
        log.info("proceed: " + proceed.getWriter());
        log.info("Around after: " + joinPoint.getSignature().getName());
        return proceed;
    }
}
