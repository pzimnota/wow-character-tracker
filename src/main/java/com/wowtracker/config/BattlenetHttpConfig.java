package com.wowtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class BattlenetHttpConfig {

    @Bean
    RestClient battlenetClient(RestClient.Builder builder, BattlenetProperties properties){
        String base = switch (properties.region()){
            case eu -> "https://eu.battle.net";
            case us -> "https://us.battle.net";
            case kr -> "https://kr.battle.net";
            case tw -> "https://tw.battle.net";
        };


        return builder
                .baseUrl(base)
                .build();
    }

    @Bean
    RestClient blizzardApiClient(RestClient.Builder builder, BattlenetProperties properties){
        String base = switch (properties.region()){
            case eu -> "https://eu.battle.net";
            case us -> "https://us.battle.net";
            case kr -> "https://kr.battle.net";
            case tw -> "https://tw.battle.net";
        };
        return builder
                .baseUrl(base)
                .build();
    }
}
