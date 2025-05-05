package com.br.photoarchival.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FileUtilsTest {

    private static final String DATA_URI = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA" +
            "AAAFCAYAAACNbyblAAAAHElEQVR42mP8z8AIAwAB/2QK5AAAAABJRU5ErkJggg==";

    @Test
    void shouldExtractFileExtension() {
        var expectedExtension = ".png";
        var actualExtension = FileUtils.extractFileExtension(DATA_URI);
        Assertions.assertThat(expectedExtension).isEqualTo(actualExtension);
    }

    @Test
    void shouldExtractFileExtensionWithInvalidDataUri() {
        var invalidDataUri = "base64,";
        var actualExtension = FileUtils.extractFileExtension(invalidDataUri);
        Assertions.assertThat(actualExtension).isNull();
    }

    @Test
    void shouldDecodeBase64FromDataUri() {
        var expectedBytes = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0
                , 0, 5, 0, 0, 0, 5, 8, 6, 0, 0, 0, -115, 111, 38, -27, 0, 0, 0, 28, 73, 68, 65, 84, 120, -38, 99, -4,
                -49, -64, 8, 3, 0, 1, -1, 100, 10, -28, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
        var actualBytes = FileUtils.decodeBase64FromDataUri(DATA_URI);
        Assertions.assertThat(expectedBytes).isEqualTo(actualBytes);
    }

    @Test
    void shouldDecodeBase64FromDataUriWithInvalidDataUri() {
        var invalidDataUri = "base64";
        var actualBytes = FileUtils.decodeBase64FromDataUri(invalidDataUri);
        Assertions.assertThat(actualBytes).isEmpty();
    }

}