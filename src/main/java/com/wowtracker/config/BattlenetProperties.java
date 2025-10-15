package com.wowtracker.config;

import com.wowtracker.domain.entity.Region;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Locale;

@ConfigurationProperties(prefix = "battlenet")
@Validated
public record BattlenetProperties(String clientId,
                                  String clientSecret,
                                  Region region,
                                  Locale locale) {

}
