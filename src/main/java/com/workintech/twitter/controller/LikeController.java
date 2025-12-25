package com.workintech.twitter.controller;

import com.workintech.twitter.dto.request.LikeRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.service.LikeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3200")
public class LikeController {
    
    private final LikeService likeService;
    
    @PostMapping("/like")
    public ResponseEntity<ApiResponse> likeTweet(
            @Valid @RequestBody LikeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApiResponse response = likeService.likeTweet(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/dislike")
    public ResponseEntity<ApiResponse> dislikeTweet(
            @Valid @RequestBody LikeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApiResponse response = likeService.dislikeTweet(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
