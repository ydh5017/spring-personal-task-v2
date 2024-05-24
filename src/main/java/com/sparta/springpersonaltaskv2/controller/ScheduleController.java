package com.sparta.springpersonaltaskv2.controller;

import com.sparta.springpersonaltaskv2.dto.PagingDto;
import com.sparta.springpersonaltaskv2.dto.ScheduleRequestDto;
import com.sparta.springpersonaltaskv2.dto.ScheduleResponseDto;
import com.sparta.springpersonaltaskv2.security.UserDetailsImpl;
import com.sparta.springpersonaltaskv2.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 일정 등록하는 메서드
     * @param requestDto 일정 요청 DTO(제목, 담당자, 내용, 비밀번호, 첨부파일)
     * @return 일정 응답 DTO
     */
    @PostMapping("/schedules")
    public ScheduleResponseDto createSchedule(
            @Valid ScheduleRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.createSchedule(requestDto, userDetails.getUser());
    }

    @GetMapping("/schedules")
    public Page<ScheduleResponseDto> getSchedules(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.getSchedules(userDetails.getUser(), page-1, size, sortBy, isAsc);
    }

    @GetMapping("/schedules/search")
    public Page<ScheduleResponseDto> searchSchedules(
            @RequestParam("query") String query,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.searchSchedules(userDetails.getUser(), query, page-1, size, sortBy, isAsc);
    }

    @PutMapping("/schedules/{id}")
    public Long updateSchedule(
            @PathVariable Long id,
            @RequestBody @Valid ScheduleRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.updateSchedule(id, requestDto, userDetails.getUser());
    }

    @DeleteMapping("/schedules/{id}")
    public Long deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.deleteSchedule(id, userDetails.getUser());
    }
}
