package com.workintech.twitter.controller;

import com.workintech.twitter.dto.request.CommentRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.dto.response.CommentResponse;
import com.workintech.twitter.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3200")
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = commentService.createComment(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = commentService.updateComment(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "Yorum silindi"));
    }
    
    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTweetId(
            @PathVariable Long tweetId) {
        List<CommentResponse> comments = commentService.findByTweetId(tweetId);
        return ResponseEntity.ok(comments);
    }
}
