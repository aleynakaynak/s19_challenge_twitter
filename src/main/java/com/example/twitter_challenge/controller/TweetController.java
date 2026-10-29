package com.example.twitter_challenge.controller;

import com.example.twitter_challenge.dto.TweetRequest;
import com.example.twitter_challenge.dto.TweetResponse;
import com.example.twitter_challenge.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tweet")
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    // [POST] /tweet  -> tweet giriş yapmış kullanıcıya ait olarak kaydedilir
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponse create(@Valid @RequestBody TweetRequest request, Authentication authentication) {
        return tweetService.create(request, authentication.getName());
    }

    // [GET] /tweet/findByUserId?userId=1
    @GetMapping("/findByUserId")
    public List<TweetResponse> findByUserId(@RequestParam Long userId) {
        return tweetService.findByUserId(userId);
    }

    // [GET] /tweet/findById?id=1
    @GetMapping("/findById")
    public TweetResponse findById(@RequestParam Long id) {
        return tweetService.findById(id);
    }

    // [PUT] /tweet/{id}
    @PutMapping("/{id}")
    public TweetResponse update(@PathVariable Long id,
                                @Valid @RequestBody TweetRequest request,
                                Authentication authentication) {
        return tweetService.update(id, request, authentication.getName());
    }

    // [DELETE] /tweet/{id}  -> sadece tweet sahibi silebilir
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        tweetService.delete(id, authentication.getName());
    }
}
