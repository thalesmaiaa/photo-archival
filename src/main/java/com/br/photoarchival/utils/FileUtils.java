package com.br.photoarchival.utils;

import com.br.photoarchival.domain.model.MediaModel;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

public class FileUtils {

    private static final String DATA_URI_PATTERN = "^data:(.*?)/([^;]+);";

    public static String extractFileExtension(String uri) {
        var pattern = Pattern.compile(DATA_URI_PATTERN);
        var matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return "." + matcher.group(2);
        }
        return null;
    }

    public static byte[] decodeBase64FromDataUri(@NotNull String dataUri) {
        return Optional.ofNullable(extractBase64FromDataUri(dataUri))
                .map(Base64.getDecoder()::decode)
                .orElse(new byte[0]);
    }

    public static MediaModel extractMediaModelFromPathName(@NotNull String filePath) {
        var filePathSegments = filePath.split("-");
        var extensionPosition = filePathSegments.length - 1;
        var fileNamePosition = filePathSegments.length - 2;
        var folderName = String.join("/", Arrays.copyOf(filePathSegments, fileNamePosition));
        var fileExtension = filePathSegments[extensionPosition];
        return new MediaModel(folderName, filePathSegments[fileNamePosition] + "." + fileExtension, null);
    }

    // example URI: data:image/png;base64,iVBORw0KGgoANSUhEUgAA...
    private static String extractBase64FromDataUri(String uri) {
        var parts = uri.split(",", 2);
        return parts.length == 2 ? parts[1] : null;
    }

}