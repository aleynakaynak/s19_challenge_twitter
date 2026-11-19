package com.example.twitter_challenge.controller;

import com.example.twitter_challenge.dto.LikeResponse;
import com.example.twitter_challenge.service.LikeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // [POST] /like/{tweetId}
    @PostMapping("/like/{tweetId}")
    public LikeResponse like(@PathVariable Long tweetId, Authentication authentication) {
        return likeService.like(tweetId, authentication.getName());
    }

    // [POST] /dislike/{tweetId}  -> atılmış like'ı geri alır
    @PostMapping("/dislike/{tweetId}")
    public LikeResponse dislike(@PathVariable Long tweetId, Authentication authentication) {
        return likeService.dislike(tweetId, authentication.getName());
    }
}
