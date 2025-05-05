package com.br.photoarchival.dto.request;

import java.util.List;

public record MetadataRequest(
        List<String> aliases,
        List<String> categories,
        double confidence,
        List<Instance> instances,
        String name,
        List<String> parents) {
    public record Instance(Double confidence, List<DominantColor> dominantColors, BoundingBox boundingBox) {
        public record DominantColor(String red, String green, String blue, String hexCode, String cssColor,
                                    String simplifiedColor, String pixelPercentage) {
        }

        public record BoundingBox(Double left, Double top, Double width, Double height) {
        }
    }

}