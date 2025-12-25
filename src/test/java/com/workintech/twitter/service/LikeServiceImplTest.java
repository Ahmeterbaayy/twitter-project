package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.LikeRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.entity.Like;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exceptions.TwitterException;
import com.workintech.twitter.repository.LikeRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Tweet tweet;
    private LikeRequest likeRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setContent("Test tweet");

        likeRequest = new LikeRequest();
        likeRequest.setTweetId(1L);
    }

    @Test
    void likeTweet_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        when(likeRepository.existsByUserIdAndTweetId(1L, 1L)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(new Like());

        ApiResponse response = likeService.likeTweet(likeRequest, "testuser");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Tweet beğenildi", response.getMessage());
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void likeTweet_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        TwitterException exception = assertThrows(TwitterException.class,
                () -> likeService.likeTweet(likeRequest, "testuser"));

        assertEquals("Kullanıcı bulunamadı", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void likeTweet_TweetNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tweetRepository.findById(1L)).thenReturn(Optional.empty());

        TwitterException exception = assertThrows(TwitterException.class,
                () -> likeService.likeTweet(likeRequest, "testuser"));

        assertEquals("Tweet bulunamadı", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void likeTweet_AlreadyLiked() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        when(likeRepository.existsByUserIdAndTweetId(1L, 1L)).thenReturn(true);

        TwitterException exception = assertThrows(TwitterException.class,
                () -> likeService.likeTweet(likeRequest, "testuser"));

        assertEquals("Bu tweet zaten beğenilmiş", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void dislikeTweet_Success() {
        Like like = new Like();
        like.setId(1L);
        like.setUser(user);
        like.setTweet(tweet);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(likeRepository.findByUserIdAndTweetId(1L, 1L)).thenReturn(Optional.of(like));

        ApiResponse response = likeService.dislikeTweet(likeRequest, "testuser");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Beğeni kaldırıldı", response.getMessage());
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void dislikeTweet_LikeNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(likeRepository.findByUserIdAndTweetId(1L, 1L)).thenReturn(Optional.empty());

        TwitterException exception = assertThrows(TwitterException.class,
                () -> likeService.dislikeTweet(likeRequest, "testuser"));

        assertEquals("Beğeni bulunamadı", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(likeRepository, never()).delete(any(Like.class));
    }
}
