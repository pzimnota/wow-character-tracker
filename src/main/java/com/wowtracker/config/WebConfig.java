package com.wowtracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RegionConverter regionConverter;
    public WebConfig(RegionConverter regionConverter) {
        this.regionConverter = regionConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(regionConverter);
    }
}
