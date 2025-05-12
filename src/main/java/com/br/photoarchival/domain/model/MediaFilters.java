package com.br.photoarchival.domain.model;

public enum MediaFilters {
    FILE_NAME("fileName", String.class),
    FOLDER_NAME("folderName", String.class),
    CATEGORY("metadata.labels.categories", String.class),
    DOMINANT_EMOTION("metadata.faces.dominantEmotion", String.class),
    MUSTACHE("metadata.faces.mustache", Boolean.class),
    BEARD("metadata.faces.beard", Boolean.class),
    SMILE("metadata.faces.smile", Boolean.class),
    LABEL_NAME("metadata.labels.name", String.class);

    private final String filterKey;
    private final Class<?> filterType;

    MediaFilters(String filterKey, Class<?> filterType) {
        this.filterKey = filterKey;
        this.filterType = filterType;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public Class<?> getFilterType() {
        return filterType;
    }
}
