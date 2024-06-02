package com.sparta.springpersonaltaskv2.service;

import com.sparta.springpersonaltaskv2.aop.ScheduleLoggingAOP;
import com.sparta.springpersonaltaskv2.dto.ScheduleRequestDto;
import com.sparta.springpersonaltaskv2.dto.ScheduleResponseDto;
import com.sparta.springpersonaltaskv2.entity.*;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import com.sparta.springpersonaltaskv2.repository.FolderRepository;
import com.sparta.springpersonaltaskv2.repository.ScheduleFolderRepository;
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
    private final FolderRepository folderRepository;
    private final ScheduleFolderRepository scheduleFolderRepository;
    private final FileService fileService;

    /**
     * 일정 등록
     * @param requestDto 일정 정보
     * @param user 회원 정보
     * @return 일정 정보
     */
    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto, User user) {
        Schedule schedule = new Schedule(requestDto, user);
        Schedule saveSchedule = scheduleRepository.save(schedule);

        // 파일 업로드
        fileService.saveFiles(schedule, requestDto.getFiles());
        return new ScheduleResponseDto(saveSchedule);
    }

    /**
     * 모든 일정 목록 조회
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param isAsc true : 오름차순, false : 내림차순
     * @return 페이징된 일정 목록
     */
    public Page<ScheduleResponseDto> getAllSchedules(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Schedule> scheduleList = scheduleRepository.findAll(pageable);

        return scheduleList.map(ScheduleResponseDto::new);
    }

    /**
     * 회원 일정 목록 조회
     * @param user 회원 정보
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param isAsc true : 오름차순, false : 내림차순
     * @return 페이징된 일정 목록
     */
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

    /**
     * 일정 조회
     * @param id 일정ID
     * @return 일정 Entity
     */
    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id).orElseThrow(
                ()-> new ScheduleException(ErrorCodeType.SCHEDULE_NOT_FOUND));
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
    public Page<ScheduleResponseDto> searchSchedules(String query, int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Schedule> scheduleList = scheduleRepository.findAllByTitleContains(query, pageable);

        return scheduleList.map(ScheduleResponseDto::new);
    }

    /**
     * 일정 수정
     * @param id 일정 ID
     * @param requestDto 일정 수정 정보
     * @param user 회원 정보
     * @return 일정 ID
     */
    @ScheduleLoggingAOP
    @Transactional
    public Long updateSchedule(Long id, ScheduleRequestDto requestDto, User user) {
        Schedule schedule = getValidatedSchedule(id, user);
        schedule.update(requestDto);
        return schedule.getId();
    }

    /**
     * 일정 삭제
     * @param id 일정 ID
     * @param user 회원 정보
     * @return 일정 ID
     */
    @Transactional
    public Long deleteSchedule(Long id, User user) {
        Schedule schedule = getValidatedSchedule(id, user);
        scheduleRepository.delete(schedule);

        fileService.deleteFiles(schedule.getFileList());
        return schedule.getId();
    }

    /**
     * 폴더에 일정 등록
     * @param scheduleId 일정 ID
     * @param folderId 폴더 ID
     * @param user 회원 정보
     */
    public void addScheduleToFolder(Long scheduleId, Long folderId, User user) {
        Schedule schedule = getValidatedSchedule(scheduleId, user);
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                ()-> new ScheduleException(ErrorCodeType.FOLDER_NOT_FOUND));

        if (!schedule.getUser().getId().equals(user.getId())
        || !folder.getUser().getId().equals(user.getId())) {
            throw new ScheduleException(ErrorCodeType.NOT_USER_SCHEDULE_AND_FOLDER);
        }

        scheduleFolderRepository.save(new ScheduleFolder(schedule, folder));
    }

    /**
     * 폴더별 일정 목록 조회
     * @param folderId 폴더 ID
     * @param page 페이지
     * @param size 개수
     * @param sortBy 정렬 기준
     * @param isAsc true : 오름차순, false : 내림차순
     * @param user 회원 정보
     * @return
     */
    public Page<ScheduleResponseDto> getSchedulesInFolder(Long folderId, int page, int size, String sortBy, boolean isAsc, User user) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Schedule> scheduleList = scheduleRepository.findAllByUserAndScheduleFolderList_FolderId(user, folderId, pageable);
        return scheduleList.map(ScheduleResponseDto::new);
    }

    /**
     * 일정 검증
     * @param id 일정 ID
     * @param user 회원 정보
     * @return 일정 Entity
     */
    private Schedule getValidatedSchedule(Long id, User user) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(()->
                new ScheduleException(ErrorCodeType.SCHEDULE_NOT_FOUND));

        if (user.getRole() == UserRoleType.USER && !schedule.getUser().getUsername().equals(user.getUsername())) {
            throw new ScheduleException(ErrorCodeType.NOT_CREATOR_OR_ADMIN);
        }
        return schedule;
    }
}
