package com.example.cashier.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class WebResponse<T> {
    private String status;
    private String message;
    private T data;
    private PaginationResponse pagination;

    public WebResponse(String status, String message, T data, PaginationResponse pagination) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

}
