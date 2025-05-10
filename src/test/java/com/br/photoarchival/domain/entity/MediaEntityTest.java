package com.br.photoarchival.domain.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

class MediaEntityTest {

    @Test
    void shouldTestMediaEntity() {
        var mediaEntity = new MediaEntity("folderName", "fileName", "url");

        Assertions.assertThat(mediaEntity.getFolderName()).isEqualTo("folderName");
        Assertions.assertThat(mediaEntity.getFileName()).isEqualTo("fileName");
        Assertions.assertThat(mediaEntity.getUrl()).isEqualTo("url");
    }

    @Test
    void shouldTestMediaEntitySetters() {
        var uploadedAt = new Date();
        var mediaEntity = new MediaEntity("folderName", "fileName", "url");

        mediaEntity.setFolderName("newFolderName");
        mediaEntity.setFileName("newFileName");
        mediaEntity.setUrl("newUrl");
        mediaEntity.setMetadata(null);
        mediaEntity.setMetadataUpdatedAt(uploadedAt);
        mediaEntity.setUploadedAt(uploadedAt);

        Assertions.assertThat(mediaEntity.getFolderName()).isEqualTo("newFolderName");
        Assertions.assertThat(mediaEntity.getFileName()).isEqualTo("newFileName");
        Assertions.assertThat(mediaEntity.getUrl()).isEqualTo("newUrl");
        Assertions.assertThat(mediaEntity.getUploadedAt()).isEqualTo(uploadedAt);
        Assertions.assertThat(mediaEntity.getMetadataUpdatedAt()).isEqualTo(uploadedAt);
        Assertions.assertThat(mediaEntity.getMetadata()).isNull();
    }
}