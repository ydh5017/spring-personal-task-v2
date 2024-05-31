package com.sparta.springpersonaltaskv2.repository;

import com.sparta.springpersonaltaskv2.entity.ScheduleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleHistoryRepository extends JpaRepository<ScheduleHistory, Long> {
}
