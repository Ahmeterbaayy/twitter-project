package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequest {
    
    @NotNull(message = "Tweet ID boş olamaz")
    private Long tweetId;
}
