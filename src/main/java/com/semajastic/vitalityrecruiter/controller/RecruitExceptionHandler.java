package com.semajastic.vitalityrecruiter.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.semajastic.vitalityrecruiter.service.RecruitValidationException;

@RestControllerAdvice
public class RecruitExceptionHandler {

  @ExceptionHandler(RecruitValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleRecruitValidation(RecruitValidationException ex) {
    return Map.of("message", ex.getMessage());
  }
}
