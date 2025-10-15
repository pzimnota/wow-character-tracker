package com.wowtracker.web.validation;

import com.wowtracker.service.RealmService;
import com.wowtracker.web.dto.CreateCharacterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public record ValidRealmInRegionValidator(
        RealmService realmService) implements ConstraintValidator<ValidRealmInRegion, CreateCharacterRequest> {


    @Override
    public boolean isValid(CreateCharacterRequest req, ConstraintValidatorContext ctx) {
        if (req == null || req.region() == null) return true;
        if (req.realmSlug() == null || req.realmSlug().isBlank()) return true;

        boolean ok = realmService.isValidRealmSlug(req.region(), req.realmSlug().trim());
        if (ok) return true;

        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate("Unknown realm for region: " + req.region())
                .addPropertyNode("realmSlug")
                .addConstraintViolation();
        return false;
    }
}
