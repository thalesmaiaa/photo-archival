package com.br.photoarchival.dto.response;

import java.util.List;

public record MediaResponse(String folderName, String fileName, String url, List<MetadataResponse> metadata) {

    public record MetadataResponse(String name, Double confidence, List<String> aliases, List<String> categories,
                                   List<String> parents, Object instances) {
    }
}
