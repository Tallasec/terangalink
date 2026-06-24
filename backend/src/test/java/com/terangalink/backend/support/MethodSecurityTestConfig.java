package com.terangalink.backend.support;

import com.terangalink.backend.security.UserSecurityService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@TestConfiguration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityTestConfig {

    @Bean(name = "userSecurityService")
    UserSecurityService userSecurityService() {
        return new UserSecurityService();
    }
}
