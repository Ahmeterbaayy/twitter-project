package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetRequest {
    
    @NotBlank(message = "Tweet içeriği boş olamaz")
    @Size(max = 280, message = "Tweet 280 karakterden fazla olamaz")
    private String content;
}
