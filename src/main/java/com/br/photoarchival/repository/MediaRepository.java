package com.br.photoarchival.repository;

import com.br.photoarchival.domain.entity.MediaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MediaRepository extends MongoRepository<MediaEntity, String> {
    Optional<MediaEntity> findByFileNameAndFolderName(String name, String folderName);
}
