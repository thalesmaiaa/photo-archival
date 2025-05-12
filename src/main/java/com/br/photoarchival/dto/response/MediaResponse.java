package com.br.photoarchival.dto.response;

public record MediaResponse(String folderName, String fileName, String url, MetadataResponse metadata,
                            String uploadedAt, String metadataUpdatedAt) {
}