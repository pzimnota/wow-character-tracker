package com.wowtracker.domain.entity;


import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "wow_character")
public class WowCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false, length = 8)
    private Region region;

    @Column(name = "realm_slug", nullable = false, length = 64)
    private String realmSlug;


    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public WowCharacter() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getRealmSlug() {
        return realmSlug;
    }

    public void setRealmSlug(String realmSlug) {
        this.realmSlug = realmSlug;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    @PreUpdate
    private void insertUpdate() {
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
