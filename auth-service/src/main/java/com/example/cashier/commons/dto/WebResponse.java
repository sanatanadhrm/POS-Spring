package com.example.cashier.commons.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class WebResponse<T> {

    public WebResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private String status;
    private String message;
    private T data;
}
