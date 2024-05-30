package com.sparta.springpersonaltaskv2.controller;

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
     * 일정 등록
     * @param requestDto 일정 등록 정보
     * @return 일정 정보
     */
    @PostMapping("/schedules")
    public ScheduleResponseDto createSchedule(
            @Valid ScheduleRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.createSchedule(requestDto, userDetails.getUser());
    }

    /**
     * 모든 일정 목록 조회
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param isAsc true : 오름차순, false : 내림차순
     * @return 페이징된 일정 목록
     */
    @GetMapping("/schedules/all")
    public Page<ScheduleResponseDto> getAllSchedules(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc) {
        return scheduleService.getAllSchedules(page-1, size, sortBy, isAsc);
    }

    /**
     * 회원 일정 목록 조회
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param isAsc true : 오름차순, false : 내림차순
     * @param userDetails 회원정보
     * @return 페이징된 일정 목록
     */
    @GetMapping("/schedules")
    public Page<ScheduleResponseDto> getSchedules(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.getSchedules(userDetails.getUser(), page-1, size, sortBy, isAsc);
    }

    /**
     * 검색 일정 목록 조회
     * @param query 검색 키워드
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param isAsc true : 오름차순, false : 내림차순
     * @return 페이징된 일정 목록
     */
    @GetMapping("/schedules/search")
    public Page<ScheduleResponseDto> searchSchedules(
            @RequestParam("query") String query,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc) {
        return scheduleService.searchSchedules(query, page-1, size, sortBy, isAsc);
    }

    /**
     * 일정 수정
     * @param id 일정ID
     * @param requestDto 일정 수정 정보
     * @param userDetails 회원정보
     * @return 일정ID
     */
    @PutMapping("/schedules/{id}")
    public Long updateSchedule(
            @PathVariable Long id,
            @RequestBody @Valid ScheduleRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.updateSchedule(id, requestDto, userDetails.getUser());
    }

    /**
     * 일정 삭제
     * @param id 일정ID
     * @param userDetails 회원정보
     * @return 일정ID
     */
    @DeleteMapping("/schedules/{id}")
    public Long deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.deleteSchedule(id, userDetails.getUser());
    }

    /**
     * 폴더에 일정 등록
     * @param scheduleId 일정ID
     * @param folderId 폴더ID
     * @param userDetails 회워정보
     */
    @PostMapping("/schedules/{scheduleId}/folder")
    public void addScheduleToFolder(
            @PathVariable Long scheduleId,
            @RequestParam Long folderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        scheduleService.addScheduleToFolder(scheduleId, folderId, userDetails.getUser());
    }

    /**
     * 폴더에 등록된 일정 목록 조회
     * @param folderId 폴더ID
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param isAsc true : 오름차순, false : 내림차순
     * @param userDetails 회원정보
     * @return 페이징된 일정 목록
     */
    @GetMapping("/folders/{folderId}/schedules")
    public Page<ScheduleResponseDto> getSchedulesInFolder(
            @PathVariable Long folderId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return scheduleService.getSchedulesInFolder(folderId, page-1, size, sortBy, isAsc, userDetails.getUser());
    }
}
