package com.br.photoarchival.dto.request;

public record MediaFiltersRequest(String fileName, String folderName, String category, String gender,
                                  String dominantEmotion, Boolean mustache, Boolean beard, String labelName,
                                  Boolean smile) {
}