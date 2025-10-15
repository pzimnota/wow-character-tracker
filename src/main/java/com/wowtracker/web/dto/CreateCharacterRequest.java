package com.wowtracker.web.dto;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.web.validation.ValidRealmInRegion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@ValidRealmInRegion
public record CreateCharacterRequest(
        @NotNull
        Region region,
        
        @NotBlank
        @Size(max = 64)
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Realm: only letters,dash(-) and numbers")
        String realmSlug,

        @NotBlank
        @Size(max = 32)
        @Pattern(regexp = "^\\p{L}+$", message = "Name: only letters")
        String name)
{

}
