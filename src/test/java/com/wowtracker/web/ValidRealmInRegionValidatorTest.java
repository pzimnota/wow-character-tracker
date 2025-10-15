package com.wowtracker.web;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.service.RealmService;
import com.wowtracker.web.dto.CreateCharacterRequest;
import com.wowtracker.web.validation.ValidRealmInRegionValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ValidRealmInRegionValidatorTest {

    RealmService realmService;
    ValidRealmInRegionValidator validator;
    ConstraintValidatorContext ctx;


    @BeforeEach
    void setup(){
        realmService = mock(RealmService.class);
        validator = new ValidRealmInRegionValidator(realmService);
        ctx = mock(ConstraintValidatorContext.class);

        var builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        var nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(ctx);
    }

    @Test
    void returnsTrue_whenServiceRecognizesRealmInRegion(){
        var request = new CreateCharacterRequest(Region.eu,"burning-legion","Zimnyy");
        when(realmService.isValidRealmSlug(Region.eu,"burning-legion")).thenReturn(true);

        var result = validator.isValid(request,ctx);

        assertThat(result).isTrue();
        verify(realmService).isValidRealmSlug(Region.eu,"burning-legion");
        verifyNoMoreInteractions(realmService);
    }

    @Test
    void returnsTrue_whenFieldsAreNull_orBlank_letOtherAnnotationHandleThis(){
        var request = new CreateCharacterRequest(null,"burning-legion","Zimnyy");
        assertThat(validator.isValid(request,ctx)).isTrue();
        verifyNoInteractions(realmService);

        var request2 = new CreateCharacterRequest(Region.eu,"     ","Zimnyy");
        assertThat(validator.isValid(request2,ctx)).isTrue();
        verifyNoInteractions(realmService);
    }


    @Test
    void returnsFalse_andAddsViolation_onUnknownRealmInRegion(){
        var request = new CreateCharacterRequest(Region.eu,"unknown","Zimnyy");
        when(realmService.isValidRealmSlug(Region.eu,"unknown")).thenReturn(false);

        var result = validator.isValid(request,ctx);
        assertThat(result).isFalse();
        verify(realmService).isValidRealmSlug(Region.eu,"unknown");
        verify(ctx).disableDefaultConstraintViolation();
        verify(ctx).buildConstraintViolationWithTemplate(contains("eu"));
    }
}
