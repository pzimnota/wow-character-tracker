package com.wowtracker.web;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.service.RealmService;
import com.wowtracker.service.dto.RealmSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RealmController.class)
public class RealmControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    RealmService realmService;

    @Test
    void listsRealmsForRegion() throws Exception{
        when(realmService.listRealms(Region.eu)).thenReturn(List.of(
                new RealmSummary(1305, "burning-legion", "Burning Legion"),
                new RealmSummary(1403, "argent-dawn", "Argent Dawn")
        ));
        mvc.perform(get("/realms").param("region", Region.eu.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("burning-legion"))
                .andExpect(jsonPath("$[1].name").value("Argent Dawn"));

    }
}
