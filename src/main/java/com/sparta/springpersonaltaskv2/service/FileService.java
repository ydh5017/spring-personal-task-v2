package com.sparta.springpersonaltaskv2.service;

import com.sparta.springpersonaltaskv2.dto.FileRequestDto;
import com.sparta.springpersonaltaskv2.dto.FileResponseDto;
import com.sparta.springpersonaltaskv2.entity.File;
import com.sparta.springpersonaltaskv2.entity.Schedule;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import com.sparta.springpersonaltaskv2.repository.FileRepository;
import com.sparta.springpersonaltaskv2.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public ResponseEntity<Resource> getFileResource(Long id) {
        FileResponseDto file = fileRepository.findById(id)
                .stream().map(FileResponseDto::new).findAny().orElseThrow(
                        ()-> new ScheduleException(ErrorCodeType.FILE_NOT_FOUND));
        Resource resource = fileUtil.readFileAsResource(file);
        try {
            String filename = URLEncoder.encode(file.getFileName(), "UTF-8");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + filename + "\";")
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize()+"")
                    .body(resource);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("filename encoding failed : " + file.getFileName());
        }
    }

    public List<FileResponseDto> findAllFilesByScheduleId(Long scheduleId) {
        return fileRepository.findAllFilesByScheduleId(scheduleId).stream().map(FileResponseDto::new).toList();
    }

    public void deleteFiles(List<File> fileList) {
        fileUtil.deleteFiles(fileList.stream().map(FileResponseDto::new).toList());
    }
}
