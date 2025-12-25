package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.RetweetRequest;
import com.workintech.twitter.dto.response.ApiResponse;

public interface RetweetService {
    
    ApiResponse createRetweet(RetweetRequest request, String username);
    
    void deleteRetweet(Long id, String username);
}
