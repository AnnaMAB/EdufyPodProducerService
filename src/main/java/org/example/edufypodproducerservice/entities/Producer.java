package org.example.edufypodproducerservice.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
public class Producer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Producer_id", columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;
    @Column(length = 50, nullable = false)
    private String name;
    @Column(length = 500, nullable = true)
    private String description;
    @Column(length = 500, nullable = true)
    private String thumbnailUrl;
    @Column(length = 500, nullable = true)
    private String imageUrl;
    @ElementCollection
    @CollectionTable(name = "producer_podcast_ids", joinColumns = @JoinColumn(name = "producer_id"))
    @Column(name = "podcast_id", columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private List<UUID> podcasts = new ArrayList<>();

    public Producer() {
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
        return "Producer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", podcasts=" + podcasts +
                '}';
    }
}
