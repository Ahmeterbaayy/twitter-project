package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.LikeRequest;
import com.workintech.twitter.dto.response.ApiResponse;

public interface LikeService {
    
    ApiResponse likeTweet(LikeRequest request, String username);
    
    ApiResponse dislikeTweet(LikeRequest request, String username);
}
