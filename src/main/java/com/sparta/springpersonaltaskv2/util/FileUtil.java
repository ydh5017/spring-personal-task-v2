package com.sparta.springpersonaltaskv2.util;

import com.sparta.springpersonaltaskv2.dto.FileRequestDto;
import com.sparta.springpersonaltaskv2.dto.FileResponseDto;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.enums.ImgFileType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileUtil {

    private final Tika tika = new Tika(); // 파일 변조 체크

    @Value("${file.upload.path}")
    private String uploadPath;

    public FileRequestDto uploadFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return null;
        }

        validImgFile(multipartFile);

        String saveName = generateSaveFilename(multipartFile.getOriginalFilename());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String uploadPath = getUploadPath(today) + File.separator + saveName;
        File uploadFile = new File(uploadPath);

        try {
            multipartFile.transferTo(uploadFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return FileRequestDto.builder()
                .fileName(multipartFile.getOriginalFilename())
                .saveName(saveName)
                .size(multipartFile.getSize())
                .build();
    }

    private void validImgFile(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String mimeType = tika.detect(inputStream);

            ImgFileType.getImgFileType(mimeType);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String generateSaveFilename(final String filename) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = StringUtils.getFilenameExtension(filename);
        return uuid + "." + extension;
    }

    private String getUploadPath(String addPath) {
        return makeDirectories(uploadPath + File.separator + addPath);
    }

    private String makeDirectories(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getPath();
    }

    public void deleteFiles(List<FileResponseDto> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        for (FileResponseDto file : files) {
            String uploadDate = file.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
            deleteFile(uploadDate, file.getSaveName());
        }
    }

    private void deleteFile(String uploadDate, String fileName) {
        String filePath = Paths.get(uploadPath, uploadDate, fileName).toString();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public Resource readFileAsResource(FileResponseDto file) {
        String uploadDate = file.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String fileName = file.getSaveName();
        Path filePath = Paths.get(uploadPath, uploadDate, fileName);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isFile()) {
                throw new ScheduleException(ErrorCodeType.FILE_NOT_FOUND);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ScheduleException(ErrorCodeType.FILE_NOT_FOUND);
        }
    }
}