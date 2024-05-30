package com.sparta.springpersonaltaskv2.service;

import com.sparta.springpersonaltaskv2.dto.FolderResponseDto;
import com.sparta.springpersonaltaskv2.entity.Folder;
import com.sparta.springpersonaltaskv2.entity.User;
import com.sparta.springpersonaltaskv2.enums.ErrorCodeType;
import com.sparta.springpersonaltaskv2.exception.ScheduleException;
import com.sparta.springpersonaltaskv2.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    /**
     * 폴더 등록
     * @param folderNames 폴더 이름 목록
     * @param user 회원 정보
     */
    public void addFolders(List<String> folderNames, User user) {
        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user, folderNames);
        List<Folder> folderList = new ArrayList<>();

        for (String folderName : folderNames) {
            if (!isExistFolderName(folderName, existFolderList)) {
                Folder folder = new Folder(folderName, user);
                folderList.add(folder);
            }else {
                throw new ScheduleException(ErrorCodeType.DUPLICATED_FOLDER);
            }
        }

        folderRepository.saveAll(folderList);
    }

    /**
     * 폴더 목록 조회
     * @param user 회원 정보
     * @return 폴더 목록
     */
    public List<FolderResponseDto> getFolders(User user) {
        return folderRepository.findAllByUser(user).stream().map(FolderResponseDto::new).toList();
    }

    /**
     * 폴더 삭제
     * @param id 폴더ID
     */
    public void deleteFolder(Long id) {
        folderRepository.deleteById(id);
    }

    /**
     * 중복되는 폴더 이름인지 체크
     * @param folderName 폴더 이름
     * @param existFolderList 폴더 목록
     * @return true or false
     */
    private boolean isExistFolderName(String folderName, List<Folder> existFolderList) {
        for (Folder existFolder : existFolderList) {
            if (folderName.equals(existFolder.getName())) {
                return true;
            }
        }
        return false;
    }
}
