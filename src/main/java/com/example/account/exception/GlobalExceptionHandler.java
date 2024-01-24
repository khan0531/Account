package com.example.account.exception;

import static com.example.account.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.example.account.type.ErrorCode.INVALID_REQUEST;

import com.example.account.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccountException.class)
  public ErrorResponse handleAccountException(AccountException e) {
    log.error("handleAccountException", e);
    return ErrorResponse.builder()
        .errorCode(e.getErrorCode())
        .errorMessage(e.getErrorMessage())
        .build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("MethodArgumentNotValidException", e);
    return ErrorResponse.builder()
        .errorCode(INVALID_REQUEST)
        .errorMessage(INVALID_REQUEST.getDescription())
        .build();
  }

  // 프라이머리 키 중복으로 사용하려고 할 때 등
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    log.error("DataIntegrityViolationException", e);
    return ErrorResponse.builder()
        .errorCode(INVALID_REQUEST)
        .errorMessage(INVALID_REQUEST.getDescription())
        .build();
  }

  @ExceptionHandler(Exception.class)
  public ErrorResponse handleException(Exception e) {
    log.error("Exception", e);
    return ErrorResponse.builder()
        .errorCode(INTERNAL_SERVER_ERROR)
        .errorMessage(INTERNAL_SERVER_ERROR.getDescription())
        .build();
  }
}
