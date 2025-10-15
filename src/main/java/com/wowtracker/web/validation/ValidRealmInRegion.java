package com.wowtracker.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRealmInRegionValidator.class)
public @interface ValidRealmInRegion {
    String message() default "Realm not found in given region";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}