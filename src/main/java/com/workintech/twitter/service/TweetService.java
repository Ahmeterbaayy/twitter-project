package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.TweetRequest;
import com.workintech.twitter.dto.response.TweetResponse;

import java.util.List;

public interface TweetService {
    
    TweetResponse createTweet(TweetRequest request, String username);
    
    List<TweetResponse> findByUserId(Long userId);
    
    List<TweetResponse> findAll();
    
    TweetResponse findById(Long id);
    
    TweetResponse updateTweet(Long id, TweetRequest request, String username);
    
    void deleteTweet(Long id, String username);
}
