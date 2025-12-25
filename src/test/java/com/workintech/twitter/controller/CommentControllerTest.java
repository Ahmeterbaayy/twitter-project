package com.workintech.twitter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workintech.twitter.dto.request.CommentRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.dto.response.CommentResponse;
import com.workintech.twitter.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CommentRequest commentRequest;
    private CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        commentRequest = new CommentRequest();
        commentRequest.setContent("Test comment");
        commentRequest.setTweetId(1L);

        commentResponse = new CommentResponse();
        commentResponse.setId(1L);
        commentResponse.setContent("Test comment");
        commentResponse.setUsername("testuser");
        commentResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createComment_Success() throws Exception {
        when(commentService.createComment(any(CommentRequest.class), eq("testuser")))
                .thenReturn(commentResponse);

        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateComment_Success() throws Exception {
        when(commentService.updateComment(eq(1L), any(CommentRequest.class), eq("testuser")))
                .thenReturn(commentResponse);

        mockMvc.perform(put("/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteComment_Success() throws Exception {
        mockMvc.perform(delete("/comment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Yorum silindi"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCommentsByTweetId_Success() throws Exception {
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.findByTweetId(1L)).thenReturn(comments);

        mockMvc.perform(get("/comment/tweet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Test comment"));
    }
}
