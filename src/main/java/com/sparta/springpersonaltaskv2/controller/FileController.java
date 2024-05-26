package com.sparta.springpersonaltaskv2.controller;

import com.sparta.springpersonaltaskv2.dto.FileResponseDto;
import com.sparta.springpersonaltaskv2.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public List<FileResponseDto> findAllFilesByScheduleId(Long scheduleId) {
        return fileService.findAllFilesByScheduleId(scheduleId);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        return fileService.getFileResource(id);
    }
}
