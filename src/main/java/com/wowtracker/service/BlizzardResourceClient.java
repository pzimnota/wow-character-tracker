package com.wowtracker.service;

import com.wowtracker.config.BattlenetProperties;
import com.wowtracker.domain.entity.Region;
import com.wowtracker.service.dto.RealmIndexResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Locale;

@Service
public class BlizzardResourceClient {
    private final RestClient api;
    private final BattlenetAuthClient authClient;
    private final BattlenetProperties properties;

    public BlizzardResourceClient(@Qualifier("blizzardApiClient")RestClient api,
                                  BattlenetAuthClient authClient,
                                  BattlenetProperties properties) {
        this.api = api;
        this.authClient = authClient;
        this.properties = properties;
        }


        public RealmIndexResponse getRealmIndex(Region region){
        var regionCode = region.name().toLowerCase(Locale.ROOT);
        var locale = resolveLocaleFor(region, (properties.locale()));
        return api.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/wow/realm/index")
                        .queryParam("namespace", "dynamic-" + regionCode)
                        .queryParam("locale", locale)
                        .build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(authClient.getAccessToken()))
                .retrieve()
                .body(RealmIndexResponse.class);
    }

    private String resolveLocaleFor(Region region, Locale configured) {
        if (configured == null) {
            return switch (region) {
                case eu -> "en_GB";
                case us -> "en_US";
                case kr -> "ko_KR";
                case tw -> "zh_TW";
            };
        }

        var code = configured.toString();
        return switch (region) {
            case eu -> isSupported(code, "en_GB", "de_DE", "es_ES", "fr_FR", "it_IT", "pt_PT", "ru_RU")
                    ? code : "en_GB";
            case us -> isSupported(code, "en_US", "es_MX", "pt_BR")
                    ? code : "en_US";
            case kr -> "ko_KR";
            case tw -> "zh_TW";
        };
    }

    private boolean isSupported(String code, String... supported) {
        for (var s : supported) {
            if (s.equalsIgnoreCase(code)) return true;
        }
        return false;
    }
}
