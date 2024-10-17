package com.attica.athens.support;

import static org.mockito.Mockito.reset;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.attica.athens.config.TestRedisConfig;
import com.attica.athens.config.TestSecurityConfig.TestCustomUserDetailsServiceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestCustomUserDetailsServiceConfig.class, TestRedisConfig.class})
@Transactional
public abstract class IntegrationTestSupport {

    protected static final String API_V1 = "api/v1";
    protected static final String API_V1_OPEN = API_V1 + "/open";
    protected static final String API_V1_AUTH = API_V1 + "/auth";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

//    @Autowired
//    @Qualifier("redisTemplate")
//    protected RedisTemplate<String, String> redisTemplate;

    @SpyBean
    protected RedisTemplate<String,String> redisTemplate;

    @BeforeEach
    void setUp(final WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
        reset(redisTemplate);
    }

    protected String readJson(final String path) throws IOException {
        return IOUtils.toString(
                resourceLoader.getResource("classpath:" + path).getInputStream(),
                StandardCharsets.UTF_8
        );
    }
}
