package com.wowtracker.config;

import com.wowtracker.domain.entity.Region;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class RegionConverter implements Converter<String, Region> {
    @Override
    public Region convert(String source) {
        var s = source.trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Query param 'region' must not be blank");
        }
        try {
            return Region.valueOf(s.toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown region: " + source);
        }
    }
}
