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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final CommentMapper commentMapper;
    
    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new TwitterException(
                "Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
        
        Tweet tweet = tweetRepository.findById(request.getTweetId())
            .orElseThrow(() -> new TwitterException(
                "Tweet bulunamadı", HttpStatus.NOT_FOUND));
        
        Comment comment = commentMapper.toEntity(request, user, tweet);
        Comment savedComment = commentRepository.save(comment);
        
        return commentMapper.toResponse(savedComment);
    }
    
    @Override
    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request, String username) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new TwitterException(
                "Yorum bulunamadı", HttpStatus.NOT_FOUND));
        
        if (!comment.getUser().getUsername().equals(username)) {
            throw new TwitterException(
                "Bu yorumu güncelleme yetkiniz yok", HttpStatus.FORBIDDEN);
        }
        
        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        
        return commentMapper.toResponse(updatedComment);
    }
    
    @Override
    @Transactional
    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new TwitterException(
                "Yorum bulunamadı", HttpStatus.NOT_FOUND));
        
        Tweet tweet = comment.getTweet();
        boolean isCommentOwner = comment.getUser().getUsername().equals(username);
        boolean isTweetOwner = tweet.getUser().getUsername().equals(username);
        
        if (!isCommentOwner && !isTweetOwner) {
            throw new TwitterException(
                "Bu yorumu silme yetkiniz yok", HttpStatus.FORBIDDEN);
        }
        
        commentRepository.delete(comment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> findByTweetId(Long tweetId) {
        if (!tweetRepository.existsById(tweetId)) {
            throw new TwitterException("Tweet bulunamadı", HttpStatus.NOT_FOUND);
        }
        
        List<Comment> comments = commentRepository.findByTweetIdOrderByCreatedAtDesc(tweetId);
        return comments.stream()
            .map(commentMapper::toResponse)
            .collect(Collectors.toList());
    }
}
