package com.br.photoarchival.factory;

import com.br.photoarchival.domain.model.MetadataModel;
import com.br.photoarchival.dto.request.MetadataRequest;
import com.br.photoarchival.dto.response.MetadataResponse;

public class MetadataDTOFactory {

    public static MetadataRequest.MetadataLabel buildMetadataLabel() {
        return new MetadataRequest.MetadataLabel(null, null, null);
    }

    public static MetadataRequest.MetadataFace buildMetadataFace() {
        return new MetadataRequest.MetadataFace(null, null, null, null,
                null, null, null, null, null, null);
    }

    public static MetadataResponse.MetadataFace buildMetadataFaceResponse() {
        return new MetadataResponse.MetadataFace(null, null, null, null,
                null, null, null, null, null, null);
    }

    public static MetadataResponse.MetadataLabel buildMetadataLabelResponse() {
        return new MetadataResponse.MetadataLabel(null, null, null);
    }

    public static MetadataModel.MetadataLabel buildMetadataLabelModel() {
        return new MetadataModel.MetadataLabel(null, null, null);
    }

    public static MetadataModel.MetadataFace buildMetadataFaceModel() {
        return new MetadataModel.MetadataFace(null, null, null, null,
                null, null, null, null, null, null);
    }
}
