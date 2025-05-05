package com.br.photoarchival.controller;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.dto.request.CreateFolderRequest;
import com.br.photoarchival.dto.request.UploadFileRequest;
import com.br.photoarchival.mapper.MediaMapper;
import com.br.photoarchival.service.MediaService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @Spy
    private MediaMapper mediaMapper = Mappers.getMapper(MediaMapper.class);

    @InjectMocks
    private MediaController mediaController;

    @Test
    void shouldUploadMedia() {
        var request = new UploadFileRequest("folder", "fileName", "file");
        var mediaModel = new MediaModel("folder", "fileName", "file");

        mediaController.uploadMedia(request);

        Mockito.verify(mediaService).uploadFile(mediaModel);
    }

    @Test
    void shouldCreateFolder() {
        var request = new CreateFolderRequest("folder");
        mediaController.createFolder(request);

        Mockito.verify(mediaService).createFolder(request.name());
    }

    @Test
    void shouldGetMedia() {
        var mediaId = UUID.randomUUID();
        var mediaEntity = new MediaEntity("folder", "fileName", "file");
        var mockedResponse = mediaMapper.toMediaResponse(mediaEntity);
        Mockito.when(mediaService.findById(mediaId)).thenReturn(mediaEntity);

        var response = mediaController.getMedia(mediaId);
        
        Mockito.verify(mediaService).findById(mediaId);
        Assertions.assertThat(response).isEqualTo(mockedResponse);
    }


}