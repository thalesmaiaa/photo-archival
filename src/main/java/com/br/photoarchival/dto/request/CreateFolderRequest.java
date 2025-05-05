package com.br.photoarchival.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateFolderRequest(@NotBlank String name) {
}
