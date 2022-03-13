package com.thoughtworks.midquiz.midquiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResult> handle(BusinessException ex) {
    ErrorResult errorResult = new ErrorResult(LocalDateTime.now().toString(), ex.getMessage(), ex.getStatus().value());
    return ResponseEntity.status(ex.getStatus()).body(errorResult);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResult> handle(MethodArgumentNotValidException ex) {
    ErrorResult errorResult = new ErrorResult(LocalDateTime.now().toString(),
            ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), 400);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
  }


}
