package com.wowtracker.service;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.service.dto.RealmIndexResponse;
import com.wowtracker.service.dto.RealmSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RealmServiceTest {
    @Mock
    BlizzardResourceClient client;

    @InjectMocks
    RealmService service;

    @Test
    void listRealms_returnsDtoFromClient(){
        var realms = List.of(
                new RealmSummary(1305, "burning-legion","Burning Legion"),
                new RealmSummary(1403,"argent-dawn", "Argent Dawn")
        );

        when(client.getRealmIndex(Region.eu)).thenReturn(new RealmIndexResponse(realms));
        var result = service.listRealms(Region.eu);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().slug()).isEqualTo("burning-legion");
        verify(client).getRealmIndex(Region.eu);
        verifyNoMoreInteractions(client);
    }

    @Test
    void isValidRealmSlug_returnsTrue_whenSlugExists(){
        when(client.getRealmIndex(Region.eu)).thenReturn(
                new RealmIndexResponse(List.of(
                        new RealmSummary(1305,"burning-legion", "Burning Legion"))));

        assertThat(service.isValidRealmSlug(Region.eu, "Burning-Legion")).isTrue();
        assertThat(service.isValidRealmSlug(Region.eu, "    burning-legion    ")).isTrue();

    }
}
