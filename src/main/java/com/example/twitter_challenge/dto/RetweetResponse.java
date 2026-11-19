package com.example.twitter_challenge.dto;

import java.time.LocalDateTime;

public record RetweetResponse(Long id, String userName, LocalDateTime createdAt) {
}
