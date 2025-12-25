package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.CommentRequest;
import com.workintech.twitter.dto.response.CommentResponse;
import com.workintech.twitter.entity.Comment;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exceptions.TwitterException;
import com.workintech.twitter.mapper.CommentMapper;
import com.workintech.twitter.repository.CommentRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    
    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private TweetRepository tweetRepository;
    
    @Mock
    private CommentMapper commentMapper;
    
    @InjectMocks
    private CommentServiceImpl commentService;
    
    private User user;
    private Tweet tweet;
    private Comment comment;
    private CommentRequest commentRequest;
    
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setContent("Test tweet");
        tweet.setUser(user);
        
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");
        comment.setUser(user);
        comment.setTweet(tweet);
        comment.setCreatedAt(LocalDateTime.now());
        
        commentRequest = new CommentRequest();
        commentRequest.setContent("Test comment");
        commentRequest.setTweetId(1L);
    }
    
    @Test
    void createComment_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        when(commentMapper.toEntity(any(), any(), any())).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(new CommentResponse());
        
        CommentResponse response = commentService.createComment(commentRequest, "testuser");
        
        assertNotNull(response);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
    
    @Test
    void createComment_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        assertThrows(TwitterException.class, () -> commentService.createComment(commentRequest, "testuser"));
    }
    
    @Test
    void deleteComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        
        commentService.deleteComment(1L, "testuser");
        
        verify(commentRepository, times(1)).delete(comment);
    }
}
