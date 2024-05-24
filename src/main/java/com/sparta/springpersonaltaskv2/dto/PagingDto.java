package com.sparta.springpersonaltaskv2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PagingDto {
    private int page;
    private int size;
    private String sortBy;
    private boolean isAsc;
}
