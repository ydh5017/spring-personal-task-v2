package com.sparta.springpersonaltaskv2.enums;

import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ImgFileType {

    JPEG("image/jpeg"),
    PJPEG("image/pjpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    BMP("image/bmp"),
    X_WINDOWS_BMP("image/x-windows-bmp");

    private String type;

    public static void getImgFileType(String fileType) {
        Arrays.stream(ImgFileType.values())
                .filter(imgFileType -> imgFileType.type.equals(fileType))
                .findAny()
                .orElseThrow(()-> new ScheduleException(ErrorCodeType.NOT_IMGFILE));
    }
}
