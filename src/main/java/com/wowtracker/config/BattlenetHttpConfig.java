package com.wowtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.Locale;


@Configuration
public class BattlenetHttpConfig {

    @Bean
    RestClient battlenetClient(RestClient.Builder builder){
        return builder
                .baseUrl("https://oauth.battle.net")
                .build();
    }

    @Bean
    RestClient blizzardApiClient(RestClient.Builder builder, BattlenetProperties properties){
        String regionCode = properties.region().name().toLowerCase(Locale.ROOT);
        String base = "https://" + regionCode + ".api.blizzard.com";
        return builder
                .baseUrl(base)
                .build();
    }
}
