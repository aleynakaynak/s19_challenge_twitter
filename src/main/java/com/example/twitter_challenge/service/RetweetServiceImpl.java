package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.RetweetResponse;
import com.example.twitter_challenge.entity.Retweet;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.RetweetRepository;
import com.example.twitter_challenge.util.DtoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetweetServiceImpl implements RetweetService {

    private final RetweetRepository retweetRepository;
    private final TweetService tweetService;
    private final UserService userService;

    public RetweetServiceImpl(RetweetRepository retweetRepository, TweetService tweetService, UserService userService) {
        this.retweetRepository = retweetRepository;
        this.tweetService = tweetService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public RetweetResponse retweet(Long tweetId, String userName) {
        User user = userService.findByUserName(userName);
        Tweet tweet = tweetService.getTweetEntity(tweetId);
        // Twitter'da olduğu gibi aynı tweet ikinci kez retweet edilemez
        if (retweetRepository.existsByUserIdAndTweetId(user.getId(), tweet.getId())) {
            throw new TwitterException("Bu tweeti zaten retweet ettiniz", HttpStatus.CONFLICT);
        }
        Retweet retweet = new Retweet(user, tweet);
        tweet.getRetweets().add(retweet);
        return DtoConverter.toRetweetResponse(retweetRepository.save(retweet));
    }

    @Override
    @Transactional
    public void delete(Long retweetId, String userName) {
        Retweet retweet = retweetRepository.findById(retweetId)
                .orElseThrow(() -> new TwitterException("Retweet bulunamadı: " + retweetId, HttpStatus.NOT_FOUND));
        if (!retweet.getUser().getUserName().equals(userName)) {
            throw new TwitterException("Sadece kendi retweetinizi geri alabilirsiniz", HttpStatus.FORBIDDEN);
        }
        retweet.getTweet().getRetweets().remove(retweet);
        retweetRepository.delete(retweet);
    }
}
