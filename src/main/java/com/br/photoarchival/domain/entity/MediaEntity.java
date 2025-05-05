package com.br.photoarchival.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "medias")
public class MediaEntity {

    @Id
    private String id;

    private String folderName;

    private String fileName;

    private String url;

    private Date uploadedAt;

    @Transient
    private List<MetadataEntity> metadata;

    public MediaEntity(String folderName, String fileName, String url) {
        this.folderName = folderName;
        this.fileName = fileName;
        this.url = url;
    }

    public MediaEntity() {
    }


    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public List<MetadataEntity> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<MetadataEntity> metadata) {
        this.metadata = metadata;
    }
}
