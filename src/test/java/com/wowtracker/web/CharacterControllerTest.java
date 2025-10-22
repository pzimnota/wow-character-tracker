package com.wowtracker.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowtracker.domain.entity.Region;
import com.wowtracker.domain.entity.WowCharacter;
import com.wowtracker.service.CharacterService;
import com.wowtracker.service.RealmService;
import com.wowtracker.web.dto.CreateCharacterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CharacterController.class)
@ActiveProfiles("test")
public class CharacterControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CharacterService service;

    @MockitoBean
    RealmService realmService;


    @BeforeEach
    public void setup() {
        when(realmService.isValidRealmSlug(any(), anyString())).thenReturn(true);
    }

    @Test
    void create_returns201_andLocation() throws Exception{

        var req = new CreateCharacterRequest(Region.eu, "burning-legion", "Zimnyy");

        when(service.addIfNotExists(Region.eu, "burning-legion", "Zimnyy"))
                .thenReturn(true);

        mvc.perform(post("/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isCreated())
                        .andExpect(header().string("Location",
                        containsString("/characters/find?region=eu&realmSlug=burning-legion")));

        verify(service).addIfNotExists(Region.eu, "burning-legion", "Zimnyy");
    }

    @Test
    void create_duplicate_returns409() throws Exception{
        var req = new CreateCharacterRequest(Region.eu, "burning-legion", "Zimnyy");
        when(service.addIfNotExists(Region.eu, "burning-legion", "Zimnyy"))
                .thenReturn(false);

        mvc.perform(post("/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/characters"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(service).addIfNotExists(Region.eu, "burning-legion", "Zimnyy");

    }

    @Test
    void create_wrongPath_returns404() throws Exception{
        mvc.perform(post("/find"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_wrongArgument_returns400() throws Exception{
        var req = new CreateCharacterRequest(Region.eu, "!!!burning-legion!!!", "Zimnyy");
        mvc.perform(post("/characters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void find_returns200() throws Exception{
        var character = getWowCharacter(Region.eu, "burning-legion", "Zimnyy");

        ReflectionTestUtils.setField(character, "id", 123L);
        ReflectionTestUtils.setField(character, "createdAt", OffsetDateTime.now(ZoneOffset.UTC));

        when(service.findByKey(Region.eu, "burning-legion", "Zimnyy"))
                .thenReturn(Optional.of(character));

        mvc.perform(get("/characters/find")
                        .param("region","eu")
                        .param("realmSlug","burning-legion")
                        .param("name","Zimnyy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zimnyy"))
                .andExpect(jsonPath("$.region").value("eu"))
                .andExpect(jsonPath("$.realmSlug").value("burning-legion"))
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(service).findByKey(Region.eu, "burning-legion", "Zimnyy");
    }

    @Test
    void find_returns404_withApiError() throws Exception{
        when(service.findByKey(Region.eu, "burning-legion", "Zimnyy"))
                .thenReturn(Optional.empty());

        mvc.perform(get("/characters/find")
                    .param("region","eu")
                    .param("realmSlug","burning-legion")
                    .param("name","Zimnyy"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/characters/find"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(service).findByKey(Region.eu, "burning-legion", "Zimnyy");
    }

    @Test
    void create_invalidSlug_returns400_withErrorsBody() throws Exception{
        var character = new CreateCharacterRequest(Region.eu, "!!!", "Zimnyy");

        mvc.perform(post("/characters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(character)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/characters"))
                .andExpect(jsonPath("$.errors[0].field").value("realmSlug"))
                .andExpect(jsonPath("$.errors[0].message", containsString("only letters,dash(-)")));

        verifyNoInteractions(service);
    }
    
    @Test
    void list_returnsCharacterListByRegion() throws Exception {
        var character = getWowCharacter(Region.eu, "burning-legion", "Zimnyy");
        ReflectionTestUtils.setField(character, "id", 1L);
        ReflectionTestUtils.setField(character, "createdAt", OffsetDateTime.now(ZoneOffset.UTC));

        var character2 = getWowCharacter(Region.eu, "burning-legion", "Eastbrook");
        ReflectionTestUtils.setField(character2, "id", 2L);
        ReflectionTestUtils.setField(character2, "createdAt", OffsetDateTime.now(ZoneOffset.UTC));


        var pageReq = PageRequest.of(0, 2, Sort.by("name").ascending());
        var page = new PageImpl<>(List.of(character, character2), pageReq, 5);

        when(service.list(Region.eu,"burning-legion", pageReq)).thenReturn(page);

        mvc.perform(get("/characters")
                    .param("region", "eu")
                    .param("realmSlug", "burning-legion")
                    .param("page", "0")
                    .param("size", "2")
                    .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Zimnyy"))
                .andExpect(jsonPath("$.content[1].name").value("Eastbrook"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5));

        verify(service).list(Region.eu,"burning-legion", pageReq);

    }

    private static WowCharacter getWowCharacter(Region region, String realmSlug, String name) {
        var character = new WowCharacter();
        character.setName(name);
        character.setRealmSlug(realmSlug);
        character.setRegion(region);
        return character;
    }
}
