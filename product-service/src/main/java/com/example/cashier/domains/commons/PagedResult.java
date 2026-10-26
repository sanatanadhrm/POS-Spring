package com.example.cashier.domains.commons;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PagedResult<T> {
    private List<T> data;
    private int page;
    private int limit;
    private long totalRows;
    private int totalPages;
    private Integer nextPage;
    private Integer prevPage;

    public PagedResult(List<T> data, int page, int limit, long totalRows, int totalPages, Integer nextPage,
            Integer prevPage) {
        this.data = data;
        this.page = page;
        this.limit = limit;
        this.totalRows = totalRows;
        this.totalPages = totalPages;
        this.nextPage = nextPage;
        this.prevPage = prevPage;
    }

    public PagedResult(List<T> data, int page, int limit, long totalRows) {
        this.data = data;
        this.page = page;
        this.limit = limit;
        this.totalRows = totalRows;
        this.totalPages = (int) Math.ceil((double) totalRows / limit);
        this.nextPage = page < totalPages ? page + 1 : null;
        this.prevPage = page > 1 ? page - 1 : null;
    }
}
