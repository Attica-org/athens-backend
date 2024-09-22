package com.attica.athens.global.auth.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {

    private String host;
    private int port;
}
