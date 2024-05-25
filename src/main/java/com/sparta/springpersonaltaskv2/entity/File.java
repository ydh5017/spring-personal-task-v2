package com.sparta.springpersonaltaskv2.entity;

import com.sparta.springpersonaltaskv2.dto.FileRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "file")
public class File extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fileName", nullable = false)
    private String fileName;

    @Column(name = "saveName", nullable = false)
    private String saveName;

    @Column(name = "size", nullable = false)
    private long size;

    @Column(name = "isDeleted")
    private int isDeleted ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    public File(FileRequestDto fileRequestDto) {
        this.fileName = fileRequestDto.getFileName();
        this.saveName = fileRequestDto.getSaveName();
        this.size = fileRequestDto.getSize();
    }
}
