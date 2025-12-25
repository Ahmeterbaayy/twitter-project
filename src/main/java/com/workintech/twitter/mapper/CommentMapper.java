package com.workintech.twitter.mapper;

import com.workintech.twitter.dto.request.CommentRequest;
import com.workintech.twitter.dto.response.CommentResponse;
import com.workintech.twitter.entity.Comment;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    
    public Comment toEntity(CommentRequest request, User user, Tweet tweet) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setTweet(tweet);
        return comment;
    }
    
    public CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            comment.getUser().getId(),
            comment.getUser().getUsername(),
            comment.getTweet().getId(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }
}
