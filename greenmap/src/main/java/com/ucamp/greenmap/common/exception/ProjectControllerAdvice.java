package com.ucamp.greenmap.common.exception;

import com.ucamp.greenmap.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ProjectControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stackTraceString = sw.toString();

        return ResponseEntity.ok(ApiResponse.error("서버 오류:\n" + stackTraceString));
    }
}
