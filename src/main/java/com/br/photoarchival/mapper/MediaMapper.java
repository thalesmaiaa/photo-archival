package com.br.photoarchival.mapper;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.domain.model.MetadataModel;
import com.br.photoarchival.dto.request.MetadataRequest;
import com.br.photoarchival.dto.request.UploadFileRequest;
import com.br.photoarchival.dto.response.MediaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    MediaResponse toMediaResponse(MediaEntity mediaEntity);

    MediaModel toMediaModel(UploadFileRequest request);

    MetadataModel toMetadataModel(MetadataRequest metadataRequest);

}
