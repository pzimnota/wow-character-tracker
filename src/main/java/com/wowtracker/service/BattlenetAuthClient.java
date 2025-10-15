package com.wowtracker.service;

import com.wowtracker.config.BattlenetProperties;
import com.wowtracker.config.TokenResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class BattlenetAuthClient {
    private final RestClient client;
    private final BattlenetProperties properties;
    private final Clock clock;
    private final AtomicReference<CachedToken> cache = new AtomicReference<>();

    private record CachedToken(String token, Instant expiresAt) {
    }

    public BattlenetAuthClient(@Qualifier("battlenetClient") RestClient client,
                               BattlenetProperties properties, Clock clock) {
        this.client = client;
        this.properties = properties;
        this.clock = clock;
    }

    TokenResponse fetchNewToken(){
        return client.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.clientId(), properties.clientSecret()))
                .body("grant_type=client_credentials")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new IllegalStateException("Battle.net auth failed: " + response.getStatusCode());
                })
                .body(TokenResponse.class);
    }

    public String getAccessToken(){
        var now = Instant.now(clock);
        var c = cache.get();
        int RELOAD_TOKEN = 30;
        if (c!=null && now.isBefore(c.expiresAt().minusSeconds(RELOAD_TOKEN))){
            return c.token;
        }
        var response = fetchNewToken();
        var expiresAt = now.plusSeconds(response.expires_in());
        var newToken = new CachedToken(response.access_token(), expiresAt);
        cache.set(newToken);
        return newToken.token();
    }
}
