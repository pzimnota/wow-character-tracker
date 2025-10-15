package com.wowtracker.service;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.domain.entity.WowCharacter;
import com.wowtracker.domain.repo.WowCharacterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class CharacterService {

    private final WowCharacterRepository repo;
    public CharacterService(WowCharacterRepository repo){
        this.repo = repo;
    }


    @Transactional
    public boolean addIfNotExists(Region region, String realmSlug, String name){
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(realmSlug,"realmSlug");
        Objects.requireNonNull(region,"region");
        String normalized = normalizeName(name);
        String normalizedRealm = normalizeRealmSlug(realmSlug);

        if (repo.existsByRegionAndRealmSlugAndNameIgnoreCase(region, normalizedRealm, normalized)){
            return false;
        }
            WowCharacter wowCharacter = new WowCharacter();
            wowCharacter.setRegion(region);
            wowCharacter.setRealmSlug(normalizedRealm);
            wowCharacter.setName(normalized);
            try {
                repo.save(wowCharacter);
                return true;
            }
            catch (org.springframework.dao.DataIntegrityViolationException e){
                return false;
            }
    }

    @Transactional(readOnly = true)
    public Optional<WowCharacter> findByKey(Region region, String realmSlug, String name){
        String normalizedName = normalizeName(name);
        String normalizedRealm = normalizeRealmSlug(realmSlug);
        return repo.findByRegionAndRealmSlugAndNameIgnoreCase(region, normalizedRealm, normalizedName);
    }


    private String normalizeName(String name){
        String normalized = name.trim();
        if (normalized.isEmpty()){
            throw new IllegalArgumentException("Name is empty");
        }
        normalized = normalized.substring(0,1).toUpperCase(Locale.ROOT) +
                normalized.substring(1).toLowerCase(Locale.ROOT);
        return normalized;
    }


    private String normalizeRealmSlug(String realm){
        String normalized = realm.trim();
        if (normalized.isEmpty()){
            throw new IllegalArgumentException("Realm is empty");
        }
        normalized = normalized.toLowerCase(Locale.ROOT);
        return normalized;
    }

    @Transactional(readOnly = true)
    public Page<WowCharacter> list(Region region, String realmSlug, Pageable pageable){
        realmSlug = normalizeRealmSlug(realmSlug);
        return repo.findByRegionAndRealmSlug(region, realmSlug, pageable);
    }
}
