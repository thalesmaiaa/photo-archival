package com.br.photoarchival.mapper;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.domain.model.MetadataModel;
import com.br.photoarchival.dto.request.MetadataRequest;
import com.br.photoarchival.dto.request.UploadFileRequest;
import com.br.photoarchival.dto.response.MediaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    @Mappings({
            @Mapping(source = "uploadedAt", target = "uploadedAt", dateFormat = "yyyy-MM-dd:HH:mm:ss"),
            @Mapping(source = "metadataUpdatedAt", target = "metadataUpdatedAt", dateFormat = "yyyy-MM-dd:HH:mm:ss"),
    })
    MediaResponse toMediaResponse(MediaEntity mediaEntity);

    MediaModel toMediaModel(UploadFileRequest request);

    MetadataModel toMetadataModel(MetadataRequest metadataRequest);

}
