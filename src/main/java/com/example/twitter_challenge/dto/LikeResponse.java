package com.example.twitter_challenge.dto;

public record LikeResponse(Long tweetId, Long userId, int likeCount, boolean liked) {
}
