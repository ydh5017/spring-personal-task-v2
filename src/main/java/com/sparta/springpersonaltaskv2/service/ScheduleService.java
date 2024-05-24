package com.sparta.springpersonaltaskv2.service;

import com.sparta.springpersonaltaskv2.dto.PagingDto;
import com.sparta.springpersonaltaskv2.dto.ScheduleRequestDto;
import com.sparta.springpersonaltaskv2.dto.ScheduleResponseDto;
import com.sparta.springpersonaltaskv2.entity.Schedule;
import com.sparta.springpersonaltaskv2.entity.User;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import com.sparta.springpersonaltaskv2.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "ScheduleService test")
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /**
     * 일정 생성 메서드
     *
     * @param requestDto 일정정보
     * @param user 회원정보
     * @return 일정정보
     */
    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto, User user) {
        Schedule saveSchedule = scheduleRepository.save(new Schedule(requestDto, user));
        return new ScheduleResponseDto(saveSchedule);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> getSchedules(User user, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UserRoleType userRoleType = user.getRole();

        Page<Schedule> scheduleList;
        if (userRoleType == UserRoleType.USER) {
            scheduleList = scheduleRepository.findAllByUser(user, pageable);
        }else {
            scheduleList = scheduleRepository.findAll(pageable);
        }

        return scheduleList.map(ScheduleResponseDto::new);
    }

    public Page<ScheduleResponseDto> searchSchedules(User user, String query, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UserRoleType userRoleType = user.getRole();

        Page<Schedule> scheduleList;
        if (userRoleType == UserRoleType.USER) {
            scheduleList = scheduleRepository.findAllByUserAndTitleContains(user, query, pageable);
        }else {
            scheduleList = scheduleRepository.findAllByTitleContains(query, pageable);
        }
        return scheduleList.map(ScheduleResponseDto::new);
    }

    @Transactional
    public Long updateSchedule(Long id, ScheduleRequestDto requestDto, User user) {
        Schedule schedule = getValidatedSchedule(id, user);
        schedule.update(requestDto);
        return schedule.getId();
    }

    public Long deleteSchedule(Long id, User user) {
        Schedule schedule = getValidatedSchedule(id, user);
        scheduleRepository.delete(schedule);
        return schedule.getId();
    }

    private Schedule getValidatedSchedule(Long id, User user) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(()->
                new ScheduleException(ErrorCodeType.SCHEDULE_NOT_FOUND));

        if (user.getRole() == UserRoleType.USER && !schedule.getUser().getUsername().equals(user.getUsername())) {
            throw new ScheduleException(ErrorCodeType.NOT_CREATOR_OR_ADMIN);
        }
        return schedule;
    }
}
