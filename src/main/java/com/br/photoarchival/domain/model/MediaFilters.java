package com.br.photoarchival.domain.model;

public enum MediaFilters {
    FILE_NAME("fileName"),
    FOLDER_NAME("folderName"),
    CATEGORY("metadata.labels.categories"),
    DOMINANT_EMOTION("metadata.faces.dominantEmotion");

    private String filterKey;

    MediaFilters(String filterKey) {
        this.filterKey = filterKey;
    }

    public String getFilterKey() {
        return filterKey;
    }
}
