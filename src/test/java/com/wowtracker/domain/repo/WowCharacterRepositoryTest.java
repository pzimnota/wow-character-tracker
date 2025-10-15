package com.wowtracker.domain.repo;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.domain.entity.WowCharacter;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WowCharacterRepositoryTest {
    @Autowired
    private WowCharacterRepository repo;
    @Autowired
    private EntityManager manager;

    @Test
    void save_shouldPersistAndSetTimestamps(){
        WowCharacter wowCharacter = new WowCharacter();
        wowCharacter.setName("Zimnyy");
        wowCharacter.setRegion(Region.eu);
        wowCharacter.setRealmSlug("burning-legion");
        var saved = repo.saveAndFlush(wowCharacter);
        manager.refresh(saved);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void existsBy_shouldBeCaseInsensitive(){
        WowCharacter wowCharacter = new WowCharacter();
        wowCharacter.setName("Zimnyy");
        wowCharacter.setRegion(Region.eu);
        wowCharacter.setRealmSlug("burning-legion");
        var saved = repo.saveAndFlush(wowCharacter);
        assertTrue(repo.existsByRegionAndRealmSlugAndNameIgnoreCase(Region.eu, "burning-legion", "zimnyy"));

        WowCharacter wowCharacterCase = new WowCharacter();
        wowCharacterCase.setName("zimnyy");
        wowCharacterCase.setRegion(Region.eu);
        wowCharacterCase.setRealmSlug("burning-legion");
        assertThrows(DataIntegrityViolationException.class, ()->
            repo.saveAndFlush(wowCharacterCase));

    }

}
