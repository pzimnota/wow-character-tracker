package com.wowtracker.service;

import com.wowtracker.config.BattlenetProperties;
import com.wowtracker.domain.entity.Region;
import com.wowtracker.service.dto.RealmSummary;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RealmService {
    private final BlizzardResourceClient client;

    public RealmService(BlizzardResourceClient client, BattlenetProperties properties){
        this.client = client;
    }

    @Cacheable(cacheNames = "realms", key = "#region")
    public List<RealmSummary> listRealms(Region region){
        var dto = client.getRealmIndex(region);
        return dto.realms();
    }

    public boolean isValidRealmSlug(Region region, String slug){
        Objects.requireNonNull(slug);
        var s = slug.trim();
        if (s.isEmpty()) return false;
        return listRealms(region).stream().anyMatch(r->r.slug().equalsIgnoreCase(s));
    }
}
