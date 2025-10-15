package com.wowtracker.domain.repo;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.domain.entity.WowCharacter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface WowCharacterRepository extends JpaRepository<WowCharacter, Long> {
    Optional<WowCharacter> findByRegionAndRealmSlugAndNameIgnoreCase(Region region, String realmSlug, String name);

    boolean existsByRegionAndRealmSlugAndNameIgnoreCase(Region region, String realmSlug, String name);

    Page<WowCharacter> findByRegionAndRealmSlug(Region region, String realmSlug, Pageable pageable);
}
