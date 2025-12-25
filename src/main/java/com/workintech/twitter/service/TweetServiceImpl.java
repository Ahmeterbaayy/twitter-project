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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TweetServiceImpl implements TweetService {
    
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final RetweetRepository retweetRepository;
    private final TweetMapper tweetMapper;
    
    @Override
    @Transactional
    public TweetResponse createTweet(TweetRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new TwitterException(
                "Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
        
        Tweet tweet = tweetMapper.toEntity(request, user);
        Tweet savedTweet = tweetRepository.save(tweet);
        
        return tweetMapper.toResponse(savedTweet, 0L, 0L, 0L);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TweetResponse> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new TwitterException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND);
        }
        
        List<Tweet> tweets = tweetRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return tweets.stream()
            .map(this::buildTweetResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TweetResponse> findAll() {
        List<Tweet> tweets = tweetRepository.findAllByOrderByCreatedAtDesc();
        return tweets.stream()
            .map(this::buildTweetResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public TweetResponse findById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
            .orElseThrow(() -> new TwitterException(
                "Tweet bulunamadı", HttpStatus.NOT_FOUND));
        
        return buildTweetResponse(tweet);
    }
    
    @Override
    @Transactional
    public TweetResponse updateTweet(Long id, TweetRequest request, String username) {
        Tweet tweet = tweetRepository.findById(id)
            .orElseThrow(() -> new TwitterException(
                "Tweet bulunamadı", HttpStatus.NOT_FOUND));
        
        if (!tweet.getUser().getUsername().equals(username)) {
            throw new TwitterException(
                "Bu tweeti güncelleme yetkiniz yok", HttpStatus.FORBIDDEN);
        }
        
        tweet.setContent(request.getContent());
        Tweet updatedTweet = tweetRepository.save(tweet);
        
        return buildTweetResponse(updatedTweet);
    }
    
    @Override
    @Transactional
    public void deleteTweet(Long id, String username) {
        Tweet tweet = tweetRepository.findById(id)
            .orElseThrow(() -> new TwitterException(
                "Tweet bulunamadı", HttpStatus.NOT_FOUND));
        
        if (!tweet.getUser().getUsername().equals(username)) {
            throw new TwitterException(
                "Bu tweeti silme yetkiniz yok", HttpStatus.FORBIDDEN);
        }
        
        tweetRepository.delete(tweet);
    }
    
    private TweetResponse buildTweetResponse(Tweet tweet) {
        Long likeCount = likeRepository.countByTweetId(tweet.getId());
        Long commentCount = (long) commentRepository.findByTweetIdOrderByCreatedAtDesc(
            tweet.getId()).size();
        Long retweetCount = retweetRepository.countByOriginalTweetId(tweet.getId());
        
        return tweetMapper.toResponse(tweet, likeCount, commentCount, retweetCount);
    }
}
