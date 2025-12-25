package com.workintech.twitter.controller;

import com.workintech.twitter.dto.request.RetweetRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.service.RetweetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweet")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3200")
public class RetweetController {
    
    private final RetweetService retweetService;
    
    @PostMapping
    public ResponseEntity<ApiResponse> createRetweet(
            @Valid @RequestBody RetweetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApiResponse response = retweetService.createRetweet(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRetweet(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        retweetService.deleteRetweet(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Retweet silindi"));
    }
}
