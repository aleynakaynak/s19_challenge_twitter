package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.LikeResponse;
import com.example.twitter_challenge.entity.Likes;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.LikeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final TweetService tweetService;
    private final UserService userService;

    public LikeServiceImpl(LikeRepository likeRepository, TweetService tweetService, UserService userService) {
        this.likeRepository = likeRepository;
        this.tweetService = tweetService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public LikeResponse like(Long tweetId, String userName) {
        User user = userService.findByUserName(userName);
        Tweet tweet = tweetService.getTweetEntity(tweetId);
        if (likeRepository.existsByUserIdAndTweetId(user.getId(), tweet.getId())) {
            throw new TwitterException("Bu tweeti zaten beğendiniz", HttpStatus.CONFLICT);
        }
        Likes like = new Likes(user, tweet);
        tweet.getLikes().add(like);
        likeRepository.save(like);
        return new LikeResponse(tweet.getId(), user.getId(), tweet.getLikes().size(), true);
    }

    @Override
    @Transactional
    public LikeResponse dislike(Long tweetId, String userName) {
        User user = userService.findByUserName(userName);
        Tweet tweet = tweetService.getTweetEntity(tweetId);
        Likes like = likeRepository.findByUserIdAndTweetId(user.getId(), tweet.getId())
                .orElseThrow(() -> new TwitterException("Bu tweeti beğenmemişsiniz", HttpStatus.NOT_FOUND));
        tweet.getLikes().remove(like);
        likeRepository.delete(like);
        return new LikeResponse(tweet.getId(), user.getId(), tweet.getLikes().size(), false);
    }
}
