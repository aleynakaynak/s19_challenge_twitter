package com.example.twitter_challenge.dto;

import java.time.LocalDateTime;

public record RetweetResponse(Long id, Long tweetId, Long userId, String userName, LocalDateTime createdAt) {
}
