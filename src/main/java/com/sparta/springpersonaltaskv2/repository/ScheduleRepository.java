package com.sparta.springpersonaltaskv2.repository;

import com.sparta.springpersonaltaskv2.entity.Schedule;
import com.sparta.springpersonaltaskv2.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Page<Schedule> findAll(Pageable pageable);

    Page<Schedule> findAllByUser(User user, Pageable pageable);

    Page<Schedule> findAllByTitleContains(String title, Pageable pageable);

    Page<Schedule> findAllByUserAndTitleContains(User user, String title, Pageable pageable);

    Page<Schedule> findAllByUserAndScheduleFolderList_FolderId(User user, Long folderId, Pageable pageable);
}
