package com.br.photoarchival.repository;

import com.br.photoarchival.domain.entity.MetadataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MetadataRepository extends MongoRepository<MetadataEntity, String> {
    List<MetadataEntity> findAllByMediaId(String mediaId);

    void deleteAllByMediaId(String mediaId);
}
