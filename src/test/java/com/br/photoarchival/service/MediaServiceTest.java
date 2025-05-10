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
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        var filePath = "src/test/resources/test.txt";
        var fileContent = Files.readString(java.nio.file.Path.of(filePath));
        var encodedContent = "data:text/txt;base64," + Base64.getEncoder().encodeToString(fileContent.getBytes());

        var mediaModel = new MediaModel("test-folder", "test", encodedContent);
        var mediaEntity = new MediaEntity();

        when(mediaRepository.findByFileNameAndFolderName(mediaModel.fileName() + ".txt", mediaModel.folderName())).thenReturn(Optional.of(mediaEntity));
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

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
    void shouldFindById() {
        var mediaId = UUID.randomUUID().toString();
        var mediaEntity = new MediaEntity();

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(mediaEntity));

        var result = mediaService.findById(mediaId);

        Assertions.assertThat(mediaEntity).isEqualTo(result);
        verify(mediaRepository).findById(mediaId);
    }

    @Test
    void shouldUpdateMediaMetadata() {
        var mediaEntity = new MediaEntity();
        var metadataModel = new MetadataModel(List.of(), List.of());
        var mediaModel = new MediaModel("test-folder", "test", "data:text/txt;base64,encodedContent");

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

        var result = mediaService.findAllMedias(null, pageable);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).hasSize(1);
        verify(mediaRepository).findAll(pageable);
        verifyNoInteractions(mongoTemplate);
    }

    @Test
    void shouldReturnAllMediasWhenCriteriaListIsEmpty() {
        var filters = new MediaFiltersRequest(null, null, null, null, null);
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
        var filters = new MediaFiltersRequest("fileName", "folderName", null, null, null);
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
}