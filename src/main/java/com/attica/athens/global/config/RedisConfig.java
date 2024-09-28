package com.attica.athens.global.config;

import com.attica.athens.global.auth.config.properties.RedisProperties;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class RedisConfig {

    private final RedisProperties redisProperties;

    /**
     * RedisConnectionFactory 빈 등록
     *
     * @return RedisConnectionFactory
     * @throws RedisConnectionException Redis 연결 실패 시 예외
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            String host = redisProperties.getHost();
            int port = redisProperties.getPort();

            log.debug("Configuring Redis connection to {}:{}", host, port);

            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
            return new LettuceConnectionFactory(redisStandaloneConfiguration);
        } catch (Exception e) {
            throw new RedisConnectionException("Failed to create Redis connection factory", e);
        }
    }

    /**
     * RedisTemplate 빈 등록
     *
     * @param connectionFactory RedisConnectionFactory
     * @return RedisTemplate<String, String>
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
