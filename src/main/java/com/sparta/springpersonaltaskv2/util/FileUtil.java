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

    /**
     * 단일 파일 업로드
     * @param multipartFile 파일
     * @return DB에 저장할 파일 정보
     */
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

    /**
     * 파일 변조 체크
     * @param file 파일
     */
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

    /**
     * 저장할 파일명 생성
     * @param filename 원본 파일명
     * @return 디스크에 저장될 파일명
     */
    private String generateSaveFilename(final String filename) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = StringUtils.getFilenameExtension(filename);
        return uuid + "." + extension;
    }

    /**
     * 업로드 경로 반환
     * @param addPath 추가 경로
     * @return 업로드 경로
     */
    private String getUploadPath(String addPath) {
        return makeDirectories(uploadPath + File.separator + addPath);
    }

    /**
     * 업로드 폴더 생성
     * @param path 업로드 경로
     * @return 업로드 경로
     */
    private String makeDirectories(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getPath();
    }

    /**
     * 다중 파일 삭제 (from Disk)
     * @param files 삭제할 파일 목록
     */
    public void deleteFiles(List<FileResponseDto> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        for (FileResponseDto file : files) {
            String uploadDate = file.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
            deleteFile(uploadDate, file.getSaveName());
        }
    }

    /**
     * 단일 파일 삭제 (from Disk)
     * @param uploadDate 추가 경로
     * @param fileName 저장한 파일명
     */
    private void deleteFile(String uploadDate, String fileName) {
        String filePath = Paths.get(uploadPath, uploadDate, fileName).toString();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 다운로드할 파일 리소스 조회
     * @param file 파일 정보
     * @return 파일 리소스
     */
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
