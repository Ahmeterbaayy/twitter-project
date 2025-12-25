package com.workintech.twitter.mapper;

import com.workintech.twitter.dto.request.TweetRequest;
import com.workintech.twitter.dto.response.TweetResponse;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TweetMapper {
    
    public Tweet toEntity(TweetRequest request, User user) {
        Tweet tweet = new Tweet();
        tweet.setContent(request.getContent());
        tweet.setUser(user);
        return tweet;
    }
    
    public TweetResponse toResponse(Tweet tweet, Long likeCount, Long commentCount, Long retweetCount) {
        return new TweetResponse(
            tweet.getId(),
            tweet.getContent(),
            tweet.getUser().getId(),
            tweet.getUser().getUsername(),
            tweet.getCreatedAt(),
            tweet.getUpdatedAt(),
            likeCount,
            commentCount,
            retweetCount
        );
    }
}
