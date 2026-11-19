package com.example.twitter_challenge.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String userName,
        LocalDateTime createdAt
) {
}
