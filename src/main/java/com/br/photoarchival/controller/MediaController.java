package com.br.photoarchival.controller;

import com.br.photoarchival.dto.request.MediaFiltersRequest;
import com.br.photoarchival.dto.request.MetadataRequest;
import com.br.photoarchival.dto.request.UploadFileRequest;
import com.br.photoarchival.dto.response.MediaResponse;
import com.br.photoarchival.mapper.MediaMapper;
import com.br.photoarchival.service.MediaService;
import com.br.photoarchival.utils.FileUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/medias")
public class MediaController {

    private final MediaService mediaService;
    private final MediaMapper mediaMapper;

    public MediaController(MediaService mediaService, MediaMapper mediaMapper) {
        this.mediaService = mediaService;
        this.mediaMapper = mediaMapper;
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadMedia(@RequestBody @Valid UploadFileRequest request) {
        mediaService.uploadFile(mediaMapper.toMediaModel(request));
    }

    @GetMapping("/{id}")
    public MediaResponse getMedia(@PathVariable String id) {
        var media = mediaService.findById(id);
        return mediaMapper.toMediaResponse(media);
    }

    @GetMapping
    public Page<MediaResponse> findAllMedias(@ModelAttribute MediaFiltersRequest filtersRequest, Pageable pageable) {
        return mediaService.findAllMedias(filtersRequest, pageable).map(mediaMapper::toMediaResponse);
    }

    @PatchMapping("/{requestFile}/metadata")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMetadata(@PathVariable String requestFile, @RequestBody(required = false) MetadataRequest metadata) {
        var mediaModel = FileUtils.extractMediaModelFromPathName(requestFile);
        var mappedMetadata = mediaMapper.toMetadataModel(metadata);
        mediaService.updateMediaMetadata(mediaModel, mappedMetadata);
    }
}
