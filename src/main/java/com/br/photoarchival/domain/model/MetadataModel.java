package com.br.photoarchival.domain.model;

import java.util.List;

public record MetadataModel(List<MetadataLabel> labels, List<MetadataFace> faces) {

    public record MetadataLabel(String name, Double confidence, List<String> categories) {
    }

    public record MetadataFace(String ageRange, String gender, String dominantEmotion, Boolean beard, Boolean mustache,
                               Boolean smile, Boolean eyeglasses, Boolean eyesOpen, Boolean sunglasses,
                               List<String> emotions) {
    }

}