package com.sparta.springpersonaltaskv2.service;

import com.sparta.springpersonaltaskv2.dto.FileRequestDto;
import com.sparta.springpersonaltaskv2.dto.FileResponseDto;
import com.sparta.springpersonaltaskv2.entity.File;
import com.sparta.springpersonaltaskv2.entity.Schedule;
import com.sparta.springpersonaltaskv2.repository.FileRepository;
import com.sparta.springpersonaltaskv2.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileUtil fileUtil;

    @Transactional
    public void saveFiles(Schedule schedule, List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }

        List<File> fileList = uploadFiles(files).stream().map(File::new).toList();

        for (File file : fileList) {
            file.setSchedule(schedule);
        }

        fileRepository.saveAll(fileList);
    }

    private List<FileRequestDto> uploadFiles(List<MultipartFile> multipartFiles) {
        List<FileRequestDto> files = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.isEmpty()) {
                continue;
            }
            files.add(fileUtil.uploadFile(multipartFile));
        }
        return files;
    }

    public List<FileResponseDto> findAllFilesByScheduleId(Long scheduleId) {
        return fileRepository.findAllFilesByScheduleId(scheduleId).stream().map(FileResponseDto::new).toList();
    }
}
