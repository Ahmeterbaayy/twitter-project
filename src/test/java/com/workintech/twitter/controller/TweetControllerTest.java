package com.workintech.twitter.controller;

import com.workintech.twitter.dto.response.TweetResponse;
import com.workintech.twitter.service.TweetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TweetControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TweetService tweetService;
    
    private TweetResponse tweetResponse;
    
    @BeforeEach
    void setUp() {
        tweetResponse = new TweetResponse();
        tweetResponse.setId(1L);
        tweetResponse.setContent("Test tweet");
        tweetResponse.setUserId(1L);
        tweetResponse.setUsername("testuser");
        tweetResponse.setCreatedAt(LocalDateTime.now());
        tweetResponse.setUpdatedAt(LocalDateTime.now());
        tweetResponse.setLikeCount(0L);
        tweetResponse.setCommentCount(0L);
        tweetResponse.setRetweetCount(0L);
    }
    
    @Test
    void findById_Success() throws Exception {
        when(tweetService.findById(1L)).thenReturn(tweetResponse);
        
        mockMvc.perform(get("/tweet/findById")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.content").value("Test tweet"));
    }
    
    @Test
    void findByUserId_Success() throws Exception {
        List<TweetResponse> tweets = Arrays.asList(tweetResponse);
        when(tweetService.findByUserId(1L)).thenReturn(tweets);
        
        mockMvc.perform(get("/tweet/findByUserId")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
