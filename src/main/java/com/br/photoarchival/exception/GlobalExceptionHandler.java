package com.br.photoarchival.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception exception) {
        var exceptionResponse = new ExceptionResponse(ExceptionMessage.INTERNAL_SERVER_ERROR.name(), List.of(exception.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<Object> handleMediaNotFoundException(MediaNotFoundException exception) {
        var exceptionResponse = new ExceptionResponse(ExceptionMessage.MEDIA_NOT_FOUND.name(), List.of(exception.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        var message = ex.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).toList();
        var exception = new ExceptionResponse(ExceptionMessage.INVALID_REQUEST.name(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageConversionException ex) {
        var message = ex.getMessage().split(":")[0];
        var exception = new ExceptionResponse(ExceptionMessage.INVALID_REQUEST.name(), List.of(message));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(AwsServiceException.class)
    public ResponseEntity<Object> handleAWSExceptions(AwsServiceException exception) {
        var details = Optional.of(List.of(exception.getMessage())).orElse(List.of());
        var exceptionResponse = new ExceptionResponse(ExceptionMessage.AWS_EXCEPTION.name(), details);
        return ResponseEntity.status(exception.statusCode()).body(exceptionResponse);
    }
}
