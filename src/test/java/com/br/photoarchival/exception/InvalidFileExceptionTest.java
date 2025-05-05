package com.br.photoarchival.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class InvalidFileExceptionTest {

    @Test
    void shouldThrowInvalidFileExceptionWithMessage() {
        var message = "Your request contains an invalid file";
        var exception = new InvalidFileException();
        Assertions.assertThat(message).isEqualTo(exception.getMessage());
    }

    @Test
    void shouldThrowInvalidFileExceptionWithoutMessage() {
        var exception = new InvalidFileException();
        Assertions.assertThat("Your request contains an invalid file").isEqualTo(exception.getMessage());
    }
}