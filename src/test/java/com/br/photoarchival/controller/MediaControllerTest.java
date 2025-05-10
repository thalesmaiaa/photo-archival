package com.br.photoarchival.controller;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.domain.model.MetadataModel;
import com.br.photoarchival.dto.request.MediaFiltersRequest;
import com.br.photoarchival.dto.request.MetadataRequest;
import com.br.photoarchival.dto.request.UploadFileRequest;
import com.br.photoarchival.factory.MetadataDTOFactory;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
    void shouldGetMedia() {
        var mediaId = UUID.randomUUID().toString();
        var mediaEntity = new MediaEntity("folder", "fileName", "file");
        var mockedResponse = mediaMapper.toMediaResponse(mediaEntity);
        Mockito.when(mediaService.findById(mediaId)).thenReturn(mediaEntity);

        var response = mediaController.getMedia(mediaId);

        Mockito.verify(mediaService).findById(mediaId);
        Assertions.assertThat(response).isEqualTo(mockedResponse);
    }

    @Test
    void shouldUpdateMediaMetadata() {
        var request = new MetadataRequest(List.of(MetadataDTOFactory.buildMetadataLabel()), List.of(MetadataDTOFactory.buildMetadataFace()));
        var mediaModel = new MediaModel("folder", "fileName.png", null);

        mediaController.updateMetadata("folder-fileName-png", request);

        Mockito.verify(mediaService).updateMediaMetadata(mediaModel, mediaMapper.toMetadataModel(request));
    }

    @Test
    void shouldFindAllMedias() {
        var filtersRequest = new MediaFiltersRequest(null, null, null, null, null);
        var pageable = Mockito.mock(Pageable.class);
        var mediaEntity = new MediaEntity("folder", "fileName", "file");
        mediaEntity.setMetadata(new MetadataModel(List.of(MetadataDTOFactory.buildMetadataLabelModel()),
                List.of(MetadataDTOFactory.buildMetadataFaceModel())));
        var mockedResponse = mediaMapper.toMediaResponse(mediaEntity);

        Mockito.when(mediaService.findAllMedias(filtersRequest, pageable)).thenReturn(new PageImpl<>(List.of(mediaEntity)));

        var response = mediaController.findAllMedias(filtersRequest, pageable);

        Mockito.verify(mediaService).findAllMedias(filtersRequest, pageable);
        Assertions.assertThat(response).containsExactly(mockedResponse);
    }


}