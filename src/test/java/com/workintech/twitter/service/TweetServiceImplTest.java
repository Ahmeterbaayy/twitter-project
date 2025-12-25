package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.TweetRequest;
import com.workintech.twitter.dto.response.TweetResponse;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exceptions.TwitterException;
import com.workintech.twitter.mapper.TweetMapper;
import com.workintech.twitter.repository.CommentRepository;
import com.workintech.twitter.repository.LikeRepository;
import com.workintech.twitter.repository.RetweetRepository;
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
class TweetServiceImplTest {
    
    @Mock
    private TweetRepository tweetRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private LikeRepository likeRepository;
    
    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private RetweetRepository retweetRepository;
    
    @Mock
    private TweetMapper tweetMapper;
    
    @InjectMocks
    private TweetServiceImpl tweetService;
    
    private User user;
    private Tweet tweet;
    private TweetRequest tweetRequest;
    
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setContent("Test tweet");
        tweet.setUser(user);
        tweet.setCreatedAt(LocalDateTime.now());
        tweet.setUpdatedAt(LocalDateTime.now());
        
        tweetRequest = new TweetRequest();
        tweetRequest.setContent("Test tweet");
    }
    
    @Test
    void createTweet_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tweetMapper.toEntity(any(TweetRequest.class), any(User.class))).thenReturn(tweet);
        when(tweetRepository.save(any(Tweet.class))).thenReturn(tweet);
        when(tweetMapper.toResponse(any(Tweet.class), anyLong(), anyLong(), anyLong()))
            .thenReturn(new TweetResponse());
        
        TweetResponse response = tweetService.createTweet(tweetRequest, "testuser");
        
        assertNotNull(response);
        verify(userRepository).findByUsername("testuser");
    }
    
    @Test
    void createTweet_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        assertThrows(TwitterException.class, () -> tweetService.createTweet(tweetRequest, "testuser"));
    }
    
    @Test
    void findById_Success() {
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        when(likeRepository.countByTweetId(1L)).thenReturn(5L);
        when(commentRepository.findByTweetIdOrderByCreatedAtDesc(1L)).thenReturn(java.util.Collections.emptyList());
        when(retweetRepository.countByOriginalTweetId(1L)).thenReturn(2L);
        when(tweetMapper.toResponse(any(Tweet.class), anyLong(), anyLong(), anyLong()))
            .thenReturn(new TweetResponse());
        
        TweetResponse response = tweetService.findById(1L);
        
        assertNotNull(response);
        verify(tweetRepository, times(1)).findById(1L);
    }
    
    @Test
    void deleteTweet_Success() {
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        
        tweetService.deleteTweet(1L, "testuser");
        
        verify(tweetRepository, times(1)).delete(tweet);
    }
    
    @Test
    void deleteTweet_Forbidden() {
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        
        assertThrows(TwitterException.class, () -> tweetService.deleteTweet(1L, "anotheruser"));
    }
}
