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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RetweetServiceImpl implements RetweetService {
    
    private final RetweetRepository retweetRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    
    @Override
    @Transactional
    public ApiResponse createRetweet(RetweetRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new TwitterException(
                "Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
        
        Tweet originalTweet = tweetRepository.findById(request.getOriginalTweetId())
            .orElseThrow(() -> new TwitterException(
                "Tweet bulunamadı", HttpStatus.NOT_FOUND));
        
        if (retweetRepository.existsByUserIdAndOriginalTweetId(
            user.getId(), request.getOriginalTweetId())) {
            throw new TwitterException(
                "Bu tweet zaten retweet edilmiş", HttpStatus.BAD_REQUEST);
        }
        
        Retweet retweet = new Retweet();
        retweet.setUser(user);
        retweet.setOriginalTweet(originalTweet);
        retweetRepository.save(retweet);
        
        return new ApiResponse(true, "Tweet retweet edildi");
    }
    
    @Override
    @Transactional
    public void deleteRetweet(Long id, String username) {
        Retweet retweet = retweetRepository.findById(id)
            .orElseThrow(() -> new TwitterException(
                "Retweet bulunamadı", HttpStatus.NOT_FOUND));
        
        if (!retweet.getUser().getUsername().equals(username)) {
            throw new TwitterException(
                "Bu retweeti silme yetkiniz yok", HttpStatus.FORBIDDEN);
        }
        
        retweetRepository.delete(retweet);
    }
}
