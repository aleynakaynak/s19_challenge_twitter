package com.example.twitter_challenge.controller;

import com.example.twitter_challenge.dto.LikeResponse;
import com.example.twitter_challenge.dto.TweetActionRequest;
import com.example.twitter_challenge.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // [POST] /like  body: { "tweetId": 1 }
    @PostMapping({"/like", "/like/"})
    public LikeResponse like(@Valid @RequestBody TweetActionRequest request, Authentication authentication) {
        return likeService.like(request.tweetId(), authentication.getName());
    }

    // [POST] /dislike  body: { "tweetId": 1 }  -> atılmış like'ı geri alır
    @PostMapping({"/dislike", "/dislike/"})
    public LikeResponse dislike(@Valid @RequestBody TweetActionRequest request, Authentication authentication) {
        return likeService.dislike(request.tweetId(), authentication.getName());
    }
}
