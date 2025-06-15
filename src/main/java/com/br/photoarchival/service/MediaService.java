package com.br.photoarchival.service;

import com.br.photoarchival.domain.entity.MediaEntity;
import com.br.photoarchival.domain.model.ImageTypes;
import com.br.photoarchival.domain.model.MediaFilters;
import com.br.photoarchival.domain.model.MediaModel;
import com.br.photoarchival.domain.model.MetadataModel;
import com.br.photoarchival.dto.request.MediaFiltersRequest;
import com.br.photoarchival.exception.InvalidFileException;
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
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;
import java.util.*;

@Service
public class MediaService {

    private final S3Client s3Client;
    private final String bucketName;
    private final MongoTemplate mongoTemplate;
    private final MediaRepository mediaRepository;

    public MediaService(S3Client s3Client,
                        MongoTemplate mongoTemplate,
                        MediaRepository mediaRepository,
                        @Value("${aws.bucket.name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.mongoTemplate = mongoTemplate;
        this.mediaRepository = mediaRepository;
    }

    public void uploadFile(MediaModel mediaModel) {
        var fileExtension = FileUtils.extractFileExtension(mediaModel.file());
        if (Objects.isNull(fileExtension)) throw new InvalidFileException();
        var imageContentType = ImageTypes.fromImageExtension(fileExtension);

        var fileName = mediaModel.fileName().concat(fileExtension);
        var folderName = mediaModel.folderName();
        var media = mediaRepository.findByFileNameAndFolderName(fileName, folderName).orElseGet(MediaEntity::new);

        var filePath = Paths.get(Optional.ofNullable(folderName).orElse(""), fileName).toString();
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .contentType(imageContentType.getMimeType())
                .build(), RequestBody.fromBytes(FileUtils.decodeBase64FromDataUri(mediaModel.file())));

        var s3Url = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();

        var fileUrl = s3Client.utilities().getUrl(s3Url).toString();

        media.setFileName(fileName);
        media.setFolderName(folderName);
        media.setUrl(fileUrl);
        media.setUploadedAt(new Date());
        mediaRepository.save(media);
    }

    public Page<MediaEntity> findAllMedias(MediaFiltersRequest filters, Pageable pageable) {
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

        var filtersMap = mountFilters(filters);

        if (filtersMap.values().stream().allMatch(Objects::isNull)) {
            return criteriaList;
        }

        filtersMap.forEach((key, filterValue) -> {
            if (Objects.nonNull(filterValue)) {
                var mediaFilter = MediaFilters.valueOf(key.toString());
                var filterKey = mediaFilter.getFilterKey();
                var filterType = mediaFilter.getFilterType();

                var criteria = filterType.equals(Boolean.class)
                        ? Criteria.where(filterKey).is(filterValue)
                        : Criteria.where(filterKey).regex(filterValue.toString(), "i");

                criteriaList.add(criteria);
            }
        });

        return criteriaList;
    }

    private static HashMap<Object, Object> mountFilters(MediaFiltersRequest filters) {
        var entries = new HashMap<>(Map.ofEntries());
        entries.put(MediaFilters.FILE_NAME, filters.fileName());
        entries.put(MediaFilters.FOLDER_NAME, filters.folderName());
        entries.put(MediaFilters.CATEGORY, filters.category());
        entries.put(MediaFilters.DOMINANT_EMOTION, filters.dominantEmotion());
        entries.put(MediaFilters.MUSTACHE, filters.mustache());
        entries.put(MediaFilters.BEARD, filters.beard());
        entries.put(MediaFilters.LABEL_NAME, filters.labelName());
        entries.put(MediaFilters.SMILE, filters.smile());
        return entries;
    }
}