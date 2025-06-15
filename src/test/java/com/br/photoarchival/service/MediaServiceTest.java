package com.br.photoarchival.service;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.domain.model.MetadataModel;
import com.br.photoarchival.dto.request.MediaFiltersRequest;
import com.br.photoarchival.exception.InvalidFileException;
import com.br.photoarchival.repository.MediaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private S3Client s3Client;

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mediaService, "bucketName", "test-bucket");
    }

    @Test
    void shouldUploadFile() throws IOException {
        var filePath = "src/test/resources/test.jpg";
        var fileContent = Files.readString(java.nio.file.Path.of(filePath));
        var encodedContent = "data:text/jpg;base64," + Base64.getEncoder().encodeToString(fileContent.getBytes());
        var mockedUtilities = mock(S3Utilities.class);
        var mockedUrl = URI.create("https://test-bucket.s3.amazonaws.com/test-folder/test.jpg").toURL();

        var mediaModel = new MediaModel("test-folder", "test", encodedContent);
        var mediaEntity = new MediaEntity();

        when(mediaRepository.findByFileNameAndFolderName(mediaModel.fileName() + ".jpg", mediaModel.folderName())).thenReturn(Optional.of(mediaEntity));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);
        when(s3Client.utilities()).thenReturn(mockedUtilities);
        when(mockedUtilities.getUrl(any(GetUrlRequest.class))).thenReturn(mockedUrl);

        mediaService.uploadFile(mediaModel);

        verify(mediaRepository).findByFileNameAndFolderName(any(), any());
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void shouldTryToUploadFileWithInvalidExtension() {
        var encodedContent = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        var mediaModel = new MediaModel("test-folder", "test", encodedContent);

        Assertions.assertThatThrownBy(() -> mediaService.uploadFile(mediaModel)).isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("Your request contains an invalid file");
    }

    @Test
    void shouldUpdateMediaMetadata() {
        var mediaEntity = new MediaEntity();
        var metadataModel = new MetadataModel(List.of(), List.of());
        var mediaModel = new MediaModel("test-folder", "test", "data:text/jpg;base64,encodedContent");

        when(mediaRepository.findByFileNameAndFolderName(mediaModel.fileName(), mediaModel.folderName())).thenReturn(Optional.of(mediaEntity));

        mediaService.updateMediaMetadata(mediaModel, metadataModel);

        verify(mediaRepository).findByFileNameAndFolderName(mediaModel.fileName(), mediaModel.folderName());
        verify(mediaRepository).save(mediaEntity);
    }

    @Test
    void shouldReturnAllMediasWhenFiltersAreNull() {
        var pageable = PageRequest.of(0, 10);
        var mediaPage = new PageImpl<>(List.of(new MediaEntity()));

        when(mediaRepository.findAll(pageable)).thenReturn(mediaPage);
        var filters = buildEmptyFilters();
        var result = mediaService.findAllMedias(filters, pageable);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).hasSize(1);
        verify(mediaRepository).findAll(pageable);
        verifyNoInteractions(mongoTemplate);
    }

    @Test
    void shouldReturnAllMediasWhenCriteriaListIsEmpty() {
        var filters = buildEmptyFilters();
        var pageable = PageRequest.of(0, 10);
        var mediaPage = new PageImpl<>(List.of(new MediaEntity()));

        when(mediaRepository.findAll(pageable)).thenReturn(mediaPage);

        var result = mediaService.findAllMedias(filters, pageable);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).hasSize(1);
        verify(mediaRepository).findAll(pageable);
        verifyNoInteractions(mongoTemplate);
    }

    @Test
    void shouldReturnFilteredMediasWhenCriteriaListIsNotEmpty() {
        var filters = new MediaFiltersRequest("fileName", "folderName", null,
                null, null, true, null, null, null);
        var pageable = PageRequest.of(0, 10);
        var mediaList = List.of(new MediaEntity());

        when(mongoTemplate.find(any(Query.class), eq(MediaEntity.class))).thenReturn(mediaList);
        when(mongoTemplate.count(any(Query.class), eq(MediaEntity.class))).thenReturn(1L);

        var result = mediaService.findAllMedias(filters, pageable);

        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).hasSize(1);
        verify(mongoTemplate).find(any(Query.class), eq(MediaEntity.class));
        verifyNoInteractions(mediaRepository);
    }

    private MediaFiltersRequest buildEmptyFilters() {
        return new MediaFiltersRequest(null, null, null, null,
                null, null, null, null, null);
    }
}