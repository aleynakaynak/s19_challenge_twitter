package com.example.twitter_challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TweetRequest(
        @NotBlank(message = "Tweet içeriği boş olamaz")
        @Size(max = 280, message = "Tweet en fazla 280 karakter olabilir")
        String content
) {
}
