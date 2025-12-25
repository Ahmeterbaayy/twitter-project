package com.workintech.twitter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workintech.twitter.dto.request.LikeRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LikeService likeService;

    private LikeRequest likeRequest;

    @BeforeEach
    void setUp() {
        likeRequest = new LikeRequest();
        likeRequest.setTweetId(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void likeTweet_Success() throws Exception {
        ApiResponse response = new ApiResponse(true, "Tweet beğenildi");
        when(likeService.likeTweet(any(LikeRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tweet beğenildi"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void dislikeTweet_Success() throws Exception {
        ApiResponse response = new ApiResponse(true, "Beğeni geri alındı");
        when(likeService.dislikeTweet(any(LikeRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/dislike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Beğeni geri alındı"));
    }
}
