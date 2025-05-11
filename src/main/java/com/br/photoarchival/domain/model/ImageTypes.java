package com.br.photoarchival.domain.model;

public enum ImageTypes {
    JPEG("image/jpeg"),
    JPG("image/jpeg"),
    PNG("image/png");

    private final String mimeType;

    ImageTypes(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static ImageTypes fromMimeType(String mimeType) {
        var fileExtension = mimeType.replace(".", "");
        for (ImageTypes type : values()) {
            if (type.name().equalsIgnoreCase(fileExtension)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported image type: " + mimeType);
    }
}
