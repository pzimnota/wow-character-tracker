package com.wowtracker.web;

import com.wowtracker.web.dto.ApiError;
import com.wowtracker.web.dto.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception,
                                                     HttpServletRequest request){

        List<ValidationError> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ApiError body = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                errors,
                Instant.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException exception,
                                                         HttpServletRequest request){
        ApiError body = new ApiError(
                exception.getStatusCode().value(),
                request.getRequestURI(),
                List.of(),
                Instant.now());
        return ResponseEntity.status(exception.getStatusCode()).body(body);
    }

}
