package com.br.photoarchival.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import software.amazon.awssdk.services.s3.model.S3Exception;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleGeneralException() {
        var exception = new Exception("General exception");
        var response = globalExceptionHandler.handleGeneralException(exception);
        var exceptionResponse = (ExceptionResponse) response.getBody();
        Assertions.assertThat(exceptionResponse).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(exceptionResponse.message()).isEqualTo(ExceptionMessage.INTERNAL_SERVER_ERROR.name());
        Assertions.assertThat(exceptionResponse.details().getFirst()).isEqualTo(exception.getMessage());
    }

    @Test
    void shouldHandleMediaNotFoundException() {
        var exception = new MediaNotFoundException("Media not found");
        var response = globalExceptionHandler.handleMediaNotFoundException(exception);
        var exceptionResponse = (ExceptionResponse) response.getBody();
        Assertions.assertThat(exceptionResponse).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertThat(exceptionResponse.message()).isEqualTo(ExceptionMessage.MEDIA_NOT_FOUND.name());
        Assertions.assertThat(exceptionResponse.details().getFirst()).isEqualTo(exception.getMessage());
    }

    @Test
    void shouldHandleMethodArgumentNotValid() {
        var bindingResult = new BeanPropertyBindingResult(null, "objectName");
        var exception = new MethodArgumentNotValidException(null, bindingResult);
        var response = globalExceptionHandler.handleMethodArgumentNotValid(exception);
        Assertions.assertThat(response).isNotNull();
        var exceptionResponse = (ExceptionResponse) response.getBody();
        Assertions.assertThat(exceptionResponse).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(exceptionResponse.message()).isEqualTo(ExceptionMessage.INVALID_REQUEST.name());
    }

    @Test
    void shouldHandleHttpMessageNotReadable() {
        var exception = new HttpMessageConversionException("Invalid request");
        var response = globalExceptionHandler.handleHttpMessageNotReadable(exception);
        Assertions.assertThat(response).isNotNull();
        var exceptionResponse = (ExceptionResponse) response.getBody();
        Assertions.assertThat(exceptionResponse).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(exceptionResponse.message()).isEqualTo(ExceptionMessage.INVALID_REQUEST.name());
    }

    @Test
    void handleAwsException() {
        var exception = S3Exception.builder().message("S3 exception occurred")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        var response = globalExceptionHandler.handleAWSExceptions(exception);
        Assertions.assertThat(response).isNotNull();
        var exceptionResponse = (ExceptionResponse) response.getBody();
        Assertions.assertThat(exceptionResponse).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(exceptionResponse.message()).isEqualTo(ExceptionMessage.AWS_EXCEPTION.name());
    }
}