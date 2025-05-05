package com.br.photoarchival.dto.request;

import jakarta.validation.constraints.NotNull;

public record UploadFileRequest(String folderName, @NotNull String fileName, @NotNull String file) {
}
