package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    
    @NotBlank(message = "Yorum içeriği boş olamaz")
    @Size(max = 280, message = "Yorum 280 karakterden fazla olamaz")
    private String content;
    
    @NotNull(message = "Tweet ID boş olamaz")
    private Long tweetId;
}
