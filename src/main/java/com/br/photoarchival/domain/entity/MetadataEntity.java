package com.br.photoarchival.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "metadata")
public class MetadataEntity {

    @Id
    private String id;

    private String mediaId;

    private String name;

    private Double confidence;

    private List<String> aliases;

    private List<String> categories;

    private List<String> parents;

    private Object instances;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public Object getInstances() {
        return instances;
    }

    public void setInstances(Object instances) {
        this.instances = instances;
    }

}