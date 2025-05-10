package com.br.photoarchival.domain.entity;

import com.br.photoarchival.domain.model.MetadataModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "medias")
public class MediaEntity {

    @Id
    private String id;

    private String folderName;

    private String fileName;

    private String url;

    private Date uploadedAt;

    private Date metadataUpdatedAt;

    private MetadataModel metadata;

    public MediaEntity(String folderName, String fileName, String url) {
        this.folderName = folderName;
        this.fileName = fileName;
        this.url = url;
    }

    public MediaEntity() {
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

    public MetadataModel getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataModel metadata) {
        this.metadata = metadata;
    }

    public Date getMetadataUpdatedAt() {
        return metadataUpdatedAt;
    }

    public void setMetadataUpdatedAt(Date metadataUpdatedAt) {
        this.metadataUpdatedAt = metadataUpdatedAt;
    }

}
