package com.br.photoarchival.controller;

import com.br.photoarchival.domain.entity.MetadataEntity;
import com.br.photoarchival.dto.request.MetadataFiltersRequest;
import com.br.photoarchival.service.MetadataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metadata")
public class MetadataController {

    private final MetadataService metadataService;

    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping
    public Page<MetadataEntity> getAllMetadata(@ModelAttribute MetadataFiltersRequest filtersRequest, Pageable pageable) {
        return metadataService.getAllMetadata(pageable, filtersRequest);
    }
}
