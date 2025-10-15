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

//        public RealmIndexResponse getRealmIndex(){
//        return getRealmIndex(properties.region());
//        }

        public RealmIndexResponse getRealmIndex(Region region){
        var regionCode = region.name().toLowerCase(Locale.ROOT);
        return api.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/wow/realm/index")
                        .queryParam("namespace", "dynamic-" + regionCode)
                        .queryParam("locale", properties.locale())
                        .build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(authClient.getAccessToken()))
                .retrieve()
                .body(RealmIndexResponse.class);
    }
}
