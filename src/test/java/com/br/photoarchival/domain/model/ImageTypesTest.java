package com.br.photoarchival.domain.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ImageTypesTest {

    @Test
    void shouldMapValidImageType() {
        var mimeType = "jpg";
        var imageType = ImageTypes.fromImageExtension(mimeType);
        Assertions.assertThat(imageType).isEqualTo(ImageTypes.JPG);
    }

    @Test
    void shouldThrowExceptionForInvalidImageType() {
        var mimeType = "invalid";
        Assertions.assertThatThrownBy(() -> ImageTypes.fromImageExtension(mimeType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported image type: invalid");
    }
}