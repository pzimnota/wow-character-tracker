package com.wowtracker.service;

import com.wowtracker.config.BattlenetProperties;
import com.wowtracker.domain.entity.Region;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BlizzardResourceClient.class)
@ActiveProfiles("test")
@EnableConfigurationProperties(BattlenetProperties.class)
@TestPropertySource(properties = {
        "battlenet.region=eu",
        "battlenet.clientId=dummy-id",
        "battlenet.clientSecret=dummy-secret",
        "battlenet.locale=pl_PL"
})
@Import(BlizzardResourceClientTest.TestHttpConfig.class)
public class BlizzardResourceClientTest {

    @TestConfiguration
    static class TestHttpConfig {
        @Bean
        RestClient blizzardApiClient(RestClient.Builder builder, BattlenetProperties props) {
            String base = switch (props.region()) {
                case eu -> "https://eu.api.blizzard.com";
                case us -> "https://us.api.blizzard.com";
                case kr -> "https://kr.api.blizzard.com";
                case tw -> "https://tw.api.blizzard.com";
            };
            return builder.baseUrl(base).build();
        }
    }

    @MockitoBean
    BattlenetAuthClient authClient;

    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private BlizzardResourceClient client;

    @Test
    void getRealmIndex_returns_realms(){
        when(authClient.getAccessToken()).thenReturn("abc123");
        server.
                expect(requestTo(Matchers.startsWith("https://eu.api.blizzard.com/data/wow/realm/index")))
                .andExpect(queryParam("namespace", "dynamic-eu"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer abc123"))
                .andRespond(withSuccess(
                        "{\"realms\":[{\"id\":1305,\"slug\":\"burning-legion\",\"name\":\"Burning Legion\"}]}",
                        MediaType.APPLICATION_JSON));

        var response = client.getRealmIndex(Region.eu);
        assertThat(response.realms()).hasSize(1);
        assertThat(response.realms().getFirst().slug()).isEqualTo("burning-legion");
        server.verify();


    }
}
