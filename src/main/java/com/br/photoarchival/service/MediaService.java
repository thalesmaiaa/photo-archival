package com.br.photoarchival.service;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.entity.MetadataEntity;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.exception.InvalidFileException;
import com.br.photoarchival.exception.MediaNotFoundException;
import com.br.photoarchival.repository.MediaRepository;
import com.br.photoarchival.repository.MetadataRepository;
import com.br.photoarchival.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MediaService {

    private final String region;
    private final S3Client s3Client;
    private final String bucketName;
    private final MediaRepository mediaRepository;
    private final MetadataRepository metadataRepository;

    private static final String S3_BASE_URL = "https://%s.s3.%s.amazonaws.com/%s";

    public MediaService(S3Client s3Client,
                        MediaRepository mediaRepository,
                        MetadataRepository metadataRepository,
                        @Value("${spring.cloud.aws.s3.bucket}") String bucketName,
                        @Value("${spring.cloud.aws.s3.region}") String region) {
        this.s3Client = s3Client;
        this.region = region;
        this.bucketName = bucketName;
        this.mediaRepository = mediaRepository;
        this.metadataRepository = metadataRepository;
    }

    public void createFolder(String folderName) {
        if (!folderName.endsWith("/")) folderName += "/";

        if (!folderExists(folderName)) {
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(folderName)
                    .build(), RequestBody.empty());
        }
    }

    public void uploadFile(MediaModel mediaModel) {
        var fileExtension = FileUtils.extractFileExtension(mediaModel.file());
        if (Objects.isNull(fileExtension)) throw new InvalidFileException();

        var fileName = mediaModel.fileName().concat(fileExtension);
        var media = mediaRepository.findByFileNameAndFolderName(fileName, mediaModel.folderName())
                .orElseGet(MediaEntity::new);

        var filePath = Paths.get(Optional.ofNullable(mediaModel.folderName()).orElse(""), fileName).toString();
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build(), RequestBody.fromBytes(FileUtils.decodeBase64FromDataUri(mediaModel.file())));


        var fileUrl = S3_BASE_URL.formatted(bucketName, region, filePath);

        media.setFileName(fileName);
        media.setFolderName(mediaModel.folderName());
        media.setUrl(fileUrl);
        media.setUploadedAt(new Date());
        mediaRepository.save(media);
    }

    public MediaEntity findById(String id) {
        var media = mediaRepository.findById(id).orElseThrow(MediaNotFoundException::new);
        var metadata = metadataRepository.findAllByMediaId(media.getId());
        media.setMetadata(metadata);
        return media;
    }

    public void updateMediaMetadata(String file, List<MetadataEntity> mappedMetadata) {
        var folderDelimiter = file.lastIndexOf("/");
        var folder = file.substring(0, folderDelimiter);
        var fileName = file.substring(folderDelimiter + 1);
        var media = mediaRepository.findByFileNameAndFolderName(fileName, folder);
        if (media.isEmpty()) return;
        var currentMedia = media.get();
        var mediaId = currentMedia.getId();

        mappedMetadata.forEach(m -> m.setMediaId(mediaId));

        metadataRepository.deleteAllByMediaId(mediaId);
        metadataRepository.saveAll(mappedMetadata);
    }

    private Boolean folderExists(String folderName) {
        var listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderName)
                .delimiter("/")
                .build();

        var listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
        return !listObjectsResponse.contents().isEmpty();
    }
}