package com.example.cashier.commons.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PaginationResponse {
    private int page;
    private int limit;
    private Integer nextPage;
    private int totalPages;
    private Long totalRows;
    private Integer prevPage;

    public PaginationResponse(int page, int limit, Integer nextPage, int totalPages, Long totalRows, Integer prevPage) {
        this.page = page;
        this.limit = limit;
        this.nextPage = nextPage;
        this.totalPages = totalPages;
        this.totalRows = totalRows;
        this.prevPage = prevPage;
    }

}