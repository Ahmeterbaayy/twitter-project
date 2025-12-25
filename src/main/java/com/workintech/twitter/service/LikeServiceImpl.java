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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LikeServiceImpl implements LikeService {
    
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    
    @Override
    @Transactional
    public ApiResponse likeTweet(LikeRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new TwitterException(
                "Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
        
        Tweet tweet = tweetRepository.findById(request.getTweetId())
            .orElseThrow(() -> new TwitterException(
                "Tweet bulunamadı", HttpStatus.NOT_FOUND));
        
        if (likeRepository.existsByUserIdAndTweetId(user.getId(), request.getTweetId())) {
            throw new TwitterException(
                "Bu tweet zaten beğenilmiş", HttpStatus.BAD_REQUEST);
        }
        
        Like like = new Like();
        like.setUser(user);
        like.setTweet(tweet);
        likeRepository.save(like);
        
        return new ApiResponse(true, "Tweet beğenildi");
    }
    
    @Override
    @Transactional
    public ApiResponse dislikeTweet(LikeRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new TwitterException(
                "Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
        
        Like like = likeRepository.findByUserIdAndTweetId(
            user.getId(), request.getTweetId())
            .orElseThrow(() -> new TwitterException(
                "Beğeni bulunamadı", HttpStatus.NOT_FOUND));
        
        likeRepository.delete(like);
        
        return new ApiResponse(true, "Beğeni kaldırıldı");
    }
}
