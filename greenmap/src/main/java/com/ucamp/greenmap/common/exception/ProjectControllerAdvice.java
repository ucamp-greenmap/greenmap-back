package com.ucamp.greenmap.common.exception;

import com.ucamp.greenmap.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        return ResponseEntity.ok(ApiResponse.error("서버 오류가 발생했습니다: " + e.getMessage()));
    }
}
