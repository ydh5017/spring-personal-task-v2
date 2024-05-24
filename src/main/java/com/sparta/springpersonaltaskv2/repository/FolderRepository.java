package com.sparta.springpersonaltaskv2.repository;

import com.sparta.springpersonaltaskv2.dto.FolderResponseDto;
import com.sparta.springpersonaltaskv2.entity.Folder;
import com.sparta.springpersonaltaskv2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUserAndNameIn(User user, List<String> folderNames);

    List<Folder> findAllByUser(User user);
}
