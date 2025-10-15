package com.wowtracker.service;

import com.wowtracker.config.BattlenetProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BattlenetAuthClient.class)
@ActiveProfiles("test")
@EnableConfigurationProperties(BattlenetProperties.class)
@TestPropertySource(properties = {
        "battlenet.region=eu",
        "battlenet.clientId=dummy-id",
        "battlenet.clientSecret=dummy-secret",
        "battlenet.locale=pl_PL"
})
@Import(BattlenetAuthClientTest.TestHttpConfig.class)
public class BattlenetAuthClientTest {

    @TestConfiguration
    static class TestHttpConfig{
        @Bean
        RestClient battlenetClient(RestClient.Builder builder, BattlenetProperties props) {
            String base = switch (props.region()) {
                case eu -> "https://eu.api.blizzard.com";
                case us -> "https://us.api.blizzard.com";
                case kr -> "https://kr.api.blizzard.com";
                case tw -> "https://tw.api.blizzard.com";
            };
            return builder.baseUrl(base).build();
        }
        @Bean
        Clock clock(){return Clock.systemUTC();}
    }

    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private BattlenetAuthClient client;

    @BeforeEach
    void resetAuthClientState() {
        ReflectionTestUtils.setField(client, "cache", new AtomicReference<>());
        ReflectionTestUtils.setField(client, "clock", Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
    }

    @AfterEach
    void verifyAndResetServer() {
        server.verify();
        server.reset();
    }

    @Test
    void fetchToken_happyPath(){
        server.expect(requestTo("https://eu.api.blizzard.com/oauth/token"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, startsWith("Basic ")))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, startsWith("application/x-www-form-urlencoded")))
                .andExpect(content().string(containsString("grant_type=client_credentials")))
                .andRespond(withSuccess(
                        "{\"access_token\":\"abc123\",\"token_type\":\"bearer\",\"expires_in\":3600,\"scope\":\"\"}",
                        MediaType.APPLICATION_JSON
                        ));

        var token = client.fetchNewToken();
        assertThat(token.access_token()).isEqualTo("abc123");
        server.verify();
    }

    @Test
    void getAccessToken_usesCacheOnSecondCall(){
        server.expect(requestTo("https://eu.api.blizzard.com/oauth/token"))
                .andRespond(withSuccess(
                        "{\"access_token\":\"abc123\",\"token_type\":\"bearer\",\"expires_in\":3600}",
                        MediaType.APPLICATION_JSON
                ));
        var token1 = client.getAccessToken();
        var token2 = client.getAccessToken();

        assertThat(token1).isEqualTo("abc123");
        assertThat(token2).isEqualTo("abc123");

        server.verify();
    }

    @Test
    void getAccessToken_refreshesWhenExpiringSoon(){
        server.expect(requestTo("https://eu.api.blizzard.com/oauth/token"))
                .andRespond(withSuccess(
                        "{\"access_token\":\"abc123\",\"token_type\":\"bearer\",\"expires_in\":60}",
                        MediaType.APPLICATION_JSON
                ));
        server.expect(requestTo("https://eu.api.blizzard.com/oauth/token"))
                .andRespond(withSuccess(
                        "{\"access_token\":\"refreshed_token\",\"token_type\":\"bearer\",\"expires_in\":60}",
                        MediaType.APPLICATION_JSON
                ));
        Clock fixed = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        ReflectionTestUtils.setField(client, "clock", fixed);


        var token1 = client.getAccessToken();
        assertThat(token1).isEqualTo("abc123");
        Clock advanced = Clock.offset(fixed, Duration.ofSeconds(40));
        ReflectionTestUtils.setField(client, "clock", advanced);


        var token2 = client.getAccessToken();
        assertThat(token2).isEqualTo("refreshed_token");

        server.verify();
    }

}
