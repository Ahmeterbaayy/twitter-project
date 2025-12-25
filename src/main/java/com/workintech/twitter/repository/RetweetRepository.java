package com.workintech.twitter.repository;

import com.workintech.twitter.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    
    Optional<Retweet> findByUserIdAndOriginalTweetId(Long userId, Long originalTweetId);
    
    boolean existsByUserIdAndOriginalTweetId(Long userId, Long originalTweetId);
    
    long countByOriginalTweetId(Long originalTweetId);
}
