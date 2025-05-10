package com.br.photoarchival.service;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.model.MediaFilters;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.domain.model.MetadataModel;
import com.br.photoarchival.dto.request.MediaFiltersRequest;
import com.br.photoarchival.exception.InvalidFileException;
import com.br.photoarchival.exception.MediaNotFoundException;
import com.br.photoarchival.repository.MediaRepository;
import com.br.photoarchival.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;
import java.util.*;

@Service
public class MediaService {

    private final String region;
    private final S3Client s3Client;
    private final String bucketName;
    private final MongoTemplate mongoTemplate;
    private final MediaRepository mediaRepository;

    private static final String S3_BASE_URL = "https://%s.s3.%s.amazonaws.com/%s";

    public MediaService(S3Client s3Client,
                        MongoTemplate mongoTemplate,
                        MediaRepository mediaRepository,
                        @Value("${spring.cloud.aws.s3.region}") String region,
                        @Value("${spring.cloud.aws.s3.bucket}") String bucketName) {
        this.region = region;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.mongoTemplate = mongoTemplate;
        this.mediaRepository = mediaRepository;
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
        return mediaRepository.findById(id).orElseThrow(MediaNotFoundException::new);
    }

    public Page<MediaEntity> findAllMedias(MediaFiltersRequest filters, Pageable pageable) {
        if (Objects.isNull(filters)) return mediaRepository.findAll(pageable);

        var criteriaList = buildMetadataCriteriaList(filters);
        if (criteriaList.isEmpty()) {
            return mediaRepository.findAll(pageable);
        }
        var combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        var query = new Query(combinedCriteria).with(pageable);
        var mediaList = mongoTemplate.find(query, MediaEntity.class);
        var total = mongoTemplate.count(query.skip(-1).limit(-1), MediaEntity.class);
        return PageableExecutionUtils.getPage(mediaList, pageable, () -> total);
    }

    public void updateMediaMetadata(MediaModel mediaModel, MetadataModel metadata) {
        var media = mediaRepository.findByFileNameAndFolderName(mediaModel.fileName(), mediaModel.folderName());
        media.ifPresent(m -> {
            m.setMetadata(metadata);
            m.setMetadataUpdatedAt(new Date());
            mediaRepository.save(m);
        });
    }

    private List<Criteria> buildMetadataCriteriaList(MediaFiltersRequest filters) {
        var criteriaList = new ArrayList<Criteria>();

        var entries = new HashMap<>(Map.ofEntries());
        entries.put(MediaFilters.FILE_NAME.getFilterKey(), filters.fileName());
        entries.put(MediaFilters.FOLDER_NAME.getFilterKey(), filters.folderName());
        entries.put(MediaFilters.CATEGORY.getFilterKey(), filters.category());
        entries.put(MediaFilters.DOMINANT_EMOTION.getFilterKey(), filters.dominantEmotion());

        entries.forEach((key, filterValue) -> {
            if (Objects.nonNull(filterValue)) {
                var criteria = Criteria.where((String) key).is(filterValue);
                criteriaList.add(criteria);
            }
        });

        return criteriaList;
    }
}