package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.CommentRequest;
import com.workintech.twitter.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    
    CommentResponse createComment(CommentRequest request, String username);
    
    CommentResponse updateComment(Long id, CommentRequest request, String username);
    
    void deleteComment(Long id, String username);
    
    List<CommentResponse> findByTweetId(Long tweetId);
}
