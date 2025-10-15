package com.wowtracker.web.dto;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.domain.entity.WowCharacter;

import java.time.OffsetDateTime;

public record CharacterResponse(Long id,
                                Region region,
                                String realmSlug,
                                String name,
                                OffsetDateTime createdAt) {

    public static CharacterResponse from(WowCharacter character) {
        return new CharacterResponse(
                character.getId(),
                character.getRegion(),
                character.getRealmSlug(),
                character.getName(),
                character.getCreatedAt());
    }
}
