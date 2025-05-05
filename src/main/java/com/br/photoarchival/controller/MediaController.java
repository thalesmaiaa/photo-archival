package com.br.photoarchival.controller;

import com.br.photoarchival.dto.request.CreateFolderRequest;
import com.br.photoarchival.dto.request.MetadataRequest;
import com.br.photoarchival.dto.request.UploadFileRequest;
import com.br.photoarchival.dto.response.MediaResponse;
import com.br.photoarchival.mapper.MediaMapper;
import com.br.photoarchival.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/media")
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

    @PostMapping("/folder")
    @ResponseStatus(HttpStatus.CREATED)
    public void createFolder(@RequestBody @Valid CreateFolderRequest request) {
        mediaService.createFolder(request.name());
    }

    @GetMapping("/{id}")
    public MediaResponse getMedia(@PathVariable String id) {
        var media = mediaService.findById(id);
        return mediaMapper.toMediaResponse(media);
    }

    @PatchMapping("/{requestFile}/metadata")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMetadata(@PathVariable String requestFile, @RequestBody(required = false) List<MetadataRequest> metadata) {
        var splitRequestFile = List.of(requestFile.split("-"));
        var fileExtension = splitRequestFile.getLast();
        var filePath = String.join("/", splitRequestFile.subList(0, splitRequestFile.size() - 1));
        var file = String.join(".", List.of(filePath, fileExtension));
        var mappedMetadata = metadata.stream()
                .map(mediaMapper::toMetadataEntity)
                .toList();
        mediaService.updateMediaMetadata(file, mappedMetadata);
    }
}
