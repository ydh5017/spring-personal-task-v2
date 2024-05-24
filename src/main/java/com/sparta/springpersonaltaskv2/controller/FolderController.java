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

    @PostMapping
    public void addFolders(
            @RequestBody FolderRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> folderNames = requestDto.getFolderNames();
        folderService.addFolders(folderNames, userDetails.getUser());
    }

    @GetMapping
    public List<FolderResponseDto> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return folderService.getFolders(userDetails.getUser());
    }

    @DeleteMapping("/{id}")
    public void deleteFolder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        folderService.deleteFolder(id);
    }
}
