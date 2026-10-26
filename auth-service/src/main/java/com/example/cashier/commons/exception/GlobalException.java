package com.example.cashier.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.cashier.commons.dto.WebResponse;

@RestControllerAdvice
public class GlobalException {
    // 1. Tangkap 400 Bad Request (Misal: Input tidak valid, Password salah)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<WebResponse<String>> handleBadRequest(BadRequestException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 2. Tangkap 401 Unauthorized (Misal: Token salah/belum login dari dalam
    // Service)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<WebResponse<String>> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // 3. Tangkap 403 Forbidden (Misal: Role tidak sesuai)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<WebResponse<String>> handleForbidden(ForbiddenException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // 4. Tangkap 404 Not Found (Misal: ID tidak ada di database)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<WebResponse<String>> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 5. Tangkap 500 Internal Server Error (Segala Error Sistem / Crash yang tidak
    // terduga)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<String>> handleGeneralException(Exception ex) {
        // Jangan tampilkan pesan asli sistem ke user, cukup berikan pesan umum
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Terjadi kesalahan pada server. Silakan coba lagi nanti.");
    }

    // -- FUNGSI BANTUAN (Helper) AGAR KODINGAN TIDAK BERULANG --
    private ResponseEntity<WebResponse<String>> buildErrorResponse(HttpStatus status, String message) {
        WebResponse<String> errorResponse = new WebResponse<String>("Error", message, null);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
