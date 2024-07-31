package com.attica.athens.support;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.attica.athens.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.SecurityConfig.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
public abstract class IntegrationTestSupport {

    protected static final String API_V1 = "api/v1";
    protected static final String API_V1_OPEN = API_V1 + "/open";
    protected static final String API_V1_AUTH = API_V1 + "/auth";

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;
    private ResourceLoader resourceLoader;

    protected IntegrationTestSupport() {
    }

    protected IntegrationTestSupport(final MockMvc mockMvc, final ObjectMapper objectMapper,
                                     final ResourceLoader resourceLoader) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @BeforeEach
    void setUp(final WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    protected String readJson(final String path) throws IOException {
        return IOUtils.toString(
                resourceLoader.getResource("classpath:" + path).getInputStream(),
                StandardCharsets.UTF_8
        );
    }
}
