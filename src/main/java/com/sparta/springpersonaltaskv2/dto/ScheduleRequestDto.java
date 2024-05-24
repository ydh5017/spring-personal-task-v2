package com.sparta.springpersonaltaskv2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ScheduleRequestDto {

    @NotEmpty(message = "제목을 작성해주세요.")
    @Size(max = 200, message = "제목은 최대 200자 이내로 작성해주세요.")
    private String title;

    @NotEmpty(message = "내용을 작성해주세요.")
    @Size(max = 400, message = "내용은 최대 400자 이내로 작성해주세요.")
    private String content;

    private List<MultipartFile> files = new ArrayList<>();

    @Builder
    public ScheduleRequestDto(String title, String content, List<MultipartFile> files) {
        this.title = title;
        this.content = content;
        this.files = files;
    }
}
