package com.revpayproject.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> data;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}