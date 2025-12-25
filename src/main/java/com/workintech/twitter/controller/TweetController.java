package com.workintech.twitter.controller;

import com.workintech.twitter.dto.request.TweetRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.dto.response.TweetResponse;
import com.workintech.twitter.service.TweetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3200")
public class TweetController {
    
    private final TweetService tweetService;
    
    @PostMapping
    public ResponseEntity<TweetResponse> createTweet(
            @Valid @RequestBody TweetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TweetResponse response = tweetService.createTweet(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/findByUserId")
    public ResponseEntity<List<TweetResponse>> findByUserId(@RequestParam Long userId) {
        List<TweetResponse> tweets = tweetService.findByUserId(userId);
        return ResponseEntity.ok(tweets);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<TweetResponse>> findAll() {
        List<TweetResponse> tweets = tweetService.findAll();
        return ResponseEntity.ok(tweets);
    }
    
    @GetMapping("/findById")
    public ResponseEntity<TweetResponse> findById(@RequestParam Long id) {
        TweetResponse tweet = tweetService.findById(id);
        return ResponseEntity.ok(tweet);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TweetResponse> updateTweet(
            @PathVariable Long id,
            @Valid @RequestBody TweetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TweetResponse response = tweetService.updateTweet(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTweet(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        tweetService.deleteTweet(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Tweet silindi"));
    }
}
