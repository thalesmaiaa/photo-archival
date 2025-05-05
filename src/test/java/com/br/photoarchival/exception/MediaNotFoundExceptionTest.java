package com.br.photoarchival.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MediaNotFoundExceptionTest {

    @Test
    void shouldThrowMediaNotFoundExceptionWithMessage() {
        var message = "Media not found";
        var exception = new MediaNotFoundException(message);
        Assertions.assertThat(message).isEqualTo(exception.getMessage());
    }

    @Test
    void shouldThrowMediaNotFoundExceptionWithoutMessage() {
        var exception = new MediaNotFoundException();
        Assertions.assertThat("Media not found").isEqualTo(exception.getMessage());
    }
}