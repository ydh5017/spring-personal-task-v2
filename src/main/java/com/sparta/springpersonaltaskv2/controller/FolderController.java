package com.sparta.springpersonaltaskv2.controller;

import com.sparta.springpersonaltaskv2.dto.FolderRequestDto;
import com.sparta.springpersonaltaskv2.dto.FolderResponseDto;
import com.sparta.springpersonaltaskv2.security.UserDetailsImpl;
import com.sparta.springpersonaltaskv2.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    /**
     * 폴더 등록
     * @param requestDto 폴더 이름 목록
     * @param userDetails 회원 정보
     */
    @PostMapping
    public void addFolders(
            @RequestBody FolderRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> folderNames = requestDto.getFolderNames();
        folderService.addFolders(folderNames, userDetails.getUser());
    }

    /**
     * 폴더 목록 조회
     * @param userDetails 회원 정보
     * @return 폴더 목록
     */
    @GetMapping
    public List<FolderResponseDto> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return folderService.getFolders(userDetails.getUser());
    }

    /**
     * 폴더 삭제
     * @param id 폴더ID
     */
    @DeleteMapping("/{id}")
    public void deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
    }
}
