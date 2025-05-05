package com.br.photoarchival.service;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.exception.InvalidFileException;
import com.br.photoarchival.repository.MediaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    private static String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        BUCKET_NAME = "test-bucket";
        ReflectionTestUtils.setField(mediaService, "bucketName", BUCKET_NAME);
    }

    @Test
    void shouldCreateFolder() {
        var folderName = "test-folder";

        var listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .prefix(folderName + "/")
                .delimiter("/")
                .build();

        var folderObject = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(folderName + "/")
                .build();

        when(s3Client.listObjectsV2(listObjectsRequest)).thenReturn(ListObjectsV2Response.builder().build());
        when(s3Client.putObject(eq(folderObject), any(RequestBody.class))).thenReturn(null);

        mediaService.createFolder(folderName);

        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
        verify(s3Client).putObject(eq(folderObject), any(RequestBody.class));
    }

    @Test
    void shouldTryToCreateExistingFolder() {
        var folderName = "test-folder/";

        var listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .prefix(folderName)
                .delimiter("/")
                .build();

        when(s3Client.listObjectsV2(listObjectsRequest)).thenReturn(ListObjectsV2Response.builder().contents(
                software.amazon.awssdk.services.s3.model.S3Object.builder()
                        .key(folderName)
                        .build()).build());

        mediaService.createFolder(folderName);

        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
        verify(s3Client, times(0)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
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
        var mediaId = UUID.randomUUID();
        var mediaEntity = new MediaEntity();

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(mediaEntity));

        var result = mediaService.findById(mediaId);

        Assertions.assertThat(mediaEntity).isEqualTo(result);
        verify(mediaRepository).findById(mediaId);
    }
}