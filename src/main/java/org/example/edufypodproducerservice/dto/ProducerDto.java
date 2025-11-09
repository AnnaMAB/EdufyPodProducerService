package org.example.edufypodproducerservice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProducerDto {

    private UUID id;
    private String name;
    private String description;
    private String thumbnailUrl;
    private String imageUrl;
    private List<UUID> podcasts = new ArrayList<>();

    public ProducerDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<UUID> getPodcasts() {
        return podcasts;
    }

    public void setPodcasts(List<UUID> podcasts) {
        this.podcasts = podcasts;
    }


    @Override
    public String toString() {
        return "ProducerDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", podcasts=" + podcasts +
                '}';
    }
}
