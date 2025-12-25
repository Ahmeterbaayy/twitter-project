package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.RetweetRequest;
import com.workintech.twitter.dto.response.ApiResponse;
import com.workintech.twitter.entity.Retweet;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exceptions.TwitterException;
import com.workintech.twitter.repository.RetweetRepository;
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
class RetweetServiceImplTest {

    @Mock
    private RetweetRepository retweetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private RetweetServiceImpl retweetService;

    private User user;
    private Tweet tweet;
    private RetweetRequest retweetRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setContent("Test tweet");

        retweetRequest = new RetweetRequest();
        retweetRequest.setOriginalTweetId(1L);
    }

    @Test
    void createRetweet_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        when(retweetRepository.existsByUserIdAndOriginalTweetId(1L, 1L)).thenReturn(false);
        when(retweetRepository.save(any(Retweet.class))).thenReturn(new Retweet());

        ApiResponse response = retweetService.createRetweet(retweetRequest, "testuser");

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Tweet retweet edildi", response.getMessage());
        verify(retweetRepository, times(1)).save(any(Retweet.class));
    }

    @Test
    void createRetweet_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        TwitterException exception = assertThrows(TwitterException.class,
                () -> retweetService.createRetweet(retweetRequest, "testuser"));

        assertEquals("Kullanıcı bulunamadı", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void createRetweet_TweetNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tweetRepository.findById(1L)).thenReturn(Optional.empty());

        TwitterException exception = assertThrows(TwitterException.class,
                () -> retweetService.createRetweet(retweetRequest, "testuser"));

        assertEquals("Tweet bulunamadı", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void createRetweet_AlreadyRetweeted() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));
        when(retweetRepository.existsByUserIdAndOriginalTweetId(1L, 1L)).thenReturn(true);

        TwitterException exception = assertThrows(TwitterException.class,
                () -> retweetService.createRetweet(retweetRequest, "testuser"));

        assertEquals("Bu tweet zaten retweet edilmiş", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(retweetRepository, never()).save(any(Retweet.class));
    }

    @Test
    void deleteRetweet_Success() {
        Retweet retweet = new Retweet();
        retweet.setId(1L);
        retweet.setUser(user);
        retweet.setOriginalTweet(tweet);

        when(retweetRepository.findById(1L)).thenReturn(Optional.of(retweet));

        retweetService.deleteRetweet(1L, "testuser");

        verify(retweetRepository, times(1)).delete(retweet);
    }

    @Test
    void deleteRetweet_NotFound() {
        when(retweetRepository.findById(1L)).thenReturn(Optional.empty());

        TwitterException exception = assertThrows(TwitterException.class,
                () -> retweetService.deleteRetweet(1L, "testuser"));

        assertEquals("Retweet bulunamadı", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void deleteRetweet_Unauthorized() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");

        Retweet retweet = new Retweet();
        retweet.setId(1L);
        retweet.setUser(anotherUser);
        retweet.setOriginalTweet(tweet);

        when(retweetRepository.findById(1L)).thenReturn(Optional.of(retweet));

        TwitterException exception = assertThrows(TwitterException.class,
                () -> retweetService.deleteRetweet(1L, "testuser"));

        assertEquals("Bu retweeti silme yetkiniz yok", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(retweetRepository, never()).delete(any(Retweet.class));
    }
}
