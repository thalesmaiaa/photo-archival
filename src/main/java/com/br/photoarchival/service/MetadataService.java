package com.br.photoarchival.service;

import com.br.photoarchival.domain.entity.MetadataEntity;
import com.br.photoarchival.dto.request.MetadataFiltersRequest;
import com.br.photoarchival.repository.MetadataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MetadataService {

    private final MongoTemplate mongoTemplate;
    private final MetadataRepository metadataRepository;

    public MetadataService(MongoTemplate mongoTemplate, MetadataRepository metadataRepository) {
        this.mongoTemplate = mongoTemplate;
        this.metadataRepository = metadataRepository;
    }

    public Page<MetadataEntity> getAllMetadata(Pageable pageable, MetadataFiltersRequest filters) {
        if (Objects.isNull(filters)) {
            return metadataRepository.findAll(pageable);
        }

        var criteriaList = buildMetadataCriteriaList(filters);

        if (!criteriaList.isEmpty()) {
            var combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            var query = new Query(combinedCriteria).with(pageable);
            var metadataList = mongoTemplate.find(query, MetadataEntity.class);
            return PageableExecutionUtils.getPage(metadataList, pageable,
                    () -> mongoTemplate.count(query.skip(-1).limit(-1), MetadataEntity.class));
        }

        return metadataRepository.findAll(pageable);
    }

    private List<Criteria> buildMetadataCriteriaList(MetadataFiltersRequest filters) {
        return Stream.of(Optional.ofNullable(filters.name()).map(name -> Criteria.where("name").is(name)),
                        Optional.ofNullable(filters.category()).map(category -> Criteria.where("categories").is(category)),
                        Optional.ofNullable(filters.parent()).map(parent -> Criteria.where("parents").is(parent)),
                        Optional.ofNullable(filters.alias()).map(alias -> Criteria.where("aliases").is(alias)))
                .flatMap(Optional::stream).toList();
    }
}
