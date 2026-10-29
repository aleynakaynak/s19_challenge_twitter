package com.example.twitter_challenge.dto;

import jakarta.validation.constraints.NotNull;

/** like, dislike ve retweet istekleri için ortak gövde: { "tweetId": 1 } */
public record TweetActionRequest(
        @NotNull(message = "tweetId zorunlu")
        Long tweetId
) {
}
