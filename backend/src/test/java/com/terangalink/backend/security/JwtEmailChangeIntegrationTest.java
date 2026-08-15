package com.terangalink.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terangalink.backend.entity.User;
import com.terangalink.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JwtEmailChangeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void token_shouldAuthenticate_afterEmailChange() throws Exception {
        // Create user
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("userA@example.com");
        user.setPassword("password");
        user.setUniversity("U");
        user.setFieldOfStudy("CS");
        user.setCity("City");
        user.setRole(com.terangalink.backend.enums.Role.USER);
        user.setEmailVerified(true);

        user = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.from(user);
        String token = jwtService.generateToken(principal);

        // Change email in DB
        user.setEmail("userB@example.com");
        userRepository.save(user);

        // Use old token to call protected endpoint
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("userB@example.com"));
    }
}
