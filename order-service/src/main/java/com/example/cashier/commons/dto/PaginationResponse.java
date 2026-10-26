package com.example.cashier.commons.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PaginationResponse {
    private Integer page;
    private Integer limit;
    private Integer nextPage;
    private Integer totalPage;
    private Long totalRows;
    private Integer prevPage;

    public PaginationResponse(Integer page, Integer limit, Integer nextPage, Integer totalPage, Long totalRows,
            Integer prevPage) {
        this.page = page;
        this.limit = limit;
        this.nextPage = nextPage;
        this.totalPage = totalPage;
        this.totalRows = totalRows;
        this.prevPage = prevPage;
    }

}