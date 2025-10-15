package com.wowtracker;

import com.wowtracker.config.BattlenetProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@EnableConfigurationProperties(BattlenetProperties.class)
@SpringBootApplication
public class WowCharacterTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WowCharacterTrackerApplication.class, args);
	}
    

}
