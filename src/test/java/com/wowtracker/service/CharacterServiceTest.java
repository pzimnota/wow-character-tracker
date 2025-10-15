package com.wowtracker.service;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.domain.entity.WowCharacter;
import com.wowtracker.domain.repo.WowCharacterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(CharacterService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CharacterServiceTest {

    @Autowired
    private WowCharacterRepository repo;
    @Autowired
    private CharacterService service;

    @Test
    void addIfNotExists_returnsTrueWhenMissing_AndFalseOnDuplicate(){
        assertTrue(service.addIfNotExists(Region.eu, "burning-legion", "zimnYY"));
        assertFalse(service.addIfNotExists(Region.eu, "burning-legion", "Zimnyy"));
        assertThat(repo.count()).isEqualTo(1L);
    }

    @Test
    void addIfNotExists_EmptyName(){
        assertThrows(IllegalArgumentException.class, ()-> service.addIfNotExists(Region.eu, "burning-legion", "  "));
    }

    @Test
    void addIfNotExists_emptyRealm(){
        assertThrows(IllegalArgumentException.class, ()-> service.addIfNotExists(Region.eu, "    ", "Zimnyy"));
    }

    @Test
    void addIfNotExists_normalizeName(){
        assertTrue(service.addIfNotExists(Region.eu, "burning-legion", "       ziMNYy    "));
        Optional<WowCharacter> wowCharacter = service.findByKey(Region.eu, "burning-legion", "zImnyY");
        assertThat(wowCharacter).isPresent();
        assertThat(wowCharacter.get().getName()).isEqualTo("Zimnyy");
    }

    @Test
    void addIfNotExists_normalizeRealm(){
        assertTrue(service.addIfNotExists(Region.eu, "   BURNING-legioN    ", "Zimnyy"));
        Optional<WowCharacter> wowCharacter = service.findByKey(Region.eu, "burning-legion", "Zimnyy");
        assertThat(wowCharacter).isPresent();
        assertThat(wowCharacter.get().getRealmSlug()).isEqualTo("burning-legion");
    }
    @Test
    void addIfNotExists_differentRealmSameName(){
        assertTrue(service.addIfNotExists(Region.eu, "burning-legion", "Zimnyy"));
        assertTrue(service.addIfNotExists(Region.eu, "argent-dawn", "Zimnyy"));
    }

}
