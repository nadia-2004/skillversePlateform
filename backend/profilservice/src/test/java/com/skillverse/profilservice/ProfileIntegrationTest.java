package com.skillverse.profilservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillverse.profilservice.model.Profile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST puis GET profils doivent fonctionner")
    void createThenListProfiles() throws Exception {
        Profile request = new Profile();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setRole("apprenant");
        request.setBio("Bio de test");
        request.setPhotoUrl("http://example.com/photo.png");

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk());

        String response = mockMvc.perform(get("/api/profiles"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Profile[] profiles = objectMapper.readValue(response, Profile[].class);
        assertThat(profiles)
                .as("la liste doit contenir au moins le profil créé")
                .isNotEmpty();
        assertThat(profiles[0].getUsername()).isEqualTo("alice");
    }
}

