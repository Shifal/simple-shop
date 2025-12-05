package com.simpleshop.exception;

import com.simpleshop.constants.ResponseMessages;
import com.simpleshop.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException ex) {
        String message = ex.getMessage();

        if (ResponseMessages.TOKEN_EXPIRED.equals(message)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, ResponseMessages.TOKEN_EXPIRED, null));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Invalid or malformed token.", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        System.err.println("ERROR:");
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, ResponseMessages.INTERNAL_SERVER_ERROR, null));
    }
}
