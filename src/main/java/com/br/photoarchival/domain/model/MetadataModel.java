package com.br.photoarchival.domain.model;

import java.util.UUID;

public record MetadataModel(UUID mediaId, String fileName, String fileSize, String fileType, String uploadedAt,
                            String labels, String url, String thumbnailUrl, Boolean facesDetected, Boolean faceCount,
                            String textDetected, String processingStatus, String processedAt) {
}
