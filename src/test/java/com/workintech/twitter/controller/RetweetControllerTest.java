package com.workintech.twitter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workintech.twitter.dto.request.RetweetRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.service.RetweetService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RetweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RetweetService retweetService;

    private RetweetRequest retweetRequest;

    @BeforeEach
    void setUp() {
        retweetRequest = new RetweetRequest();
        retweetRequest.setOriginalTweetId(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createRetweet_Success() throws Exception {
        ApiResponse response = new ApiResponse(true, "Tweet retweet edildi");
        when(retweetService.createRetweet(any(RetweetRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/retweet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(retweetRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tweet retweet edildi"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteRetweet_Success() throws Exception {
        mockMvc.perform(delete("/retweet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Retweet silindi"));
    }
}
