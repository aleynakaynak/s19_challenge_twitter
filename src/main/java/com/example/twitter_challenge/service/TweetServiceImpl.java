package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.TweetRequest;
import com.example.twitter_challenge.dto.TweetResponse;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.TweetRepository;
import com.example.twitter_challenge.util.DtoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserService userService;

    public TweetServiceImpl(TweetRepository tweetRepository, UserService userService) {
        this.tweetRepository = tweetRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public TweetResponse create(TweetRequest request, String userName) {
        // Tweet her zaman giriş yapmış kullanıcıya bağlanır: anonim tweet olmaz
        User user = userService.findByUserName(userName);
        Tweet tweet = new Tweet(request.content(), user);
        return DtoConverter.toTweetResponse(tweetRepository.save(tweet));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TweetResponse> findByUserId(Long userId) {
        userService.findById(userId); // kullanıcı yoksa 404
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(DtoConverter::toTweetResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TweetResponse findById(Long id) {
        return DtoConverter.toTweetResponse(getTweetEntity(id));
    }

    @Override
    @Transactional
    public TweetResponse update(Long id, TweetRequest request, String userName) {
        Tweet tweet = getTweetEntity(id);
        checkOwner(tweet, userName, "Sadece kendi tweetinizi güncelleyebilirsiniz");
        tweet.setContent(request.content());
        return DtoConverter.toTweetResponse(tweetRepository.save(tweet));
    }

    @Override
    @Transactional
    public void delete(Long id, String userName) {
        Tweet tweet = getTweetEntity(id);
        checkOwner(tweet, userName, "Sadece tweet sahibi tweeti silebilir");
        tweetRepository.delete(tweet);
    }

    @Override
    public Tweet getTweetEntity(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new TwitterException("Tweet bulunamadı: " + id, HttpStatus.NOT_FOUND));
    }

    private void checkOwner(Tweet tweet, String userName, String message) {
        if (!tweet.getUser().getUserName().equals(userName)) {
            throw new TwitterException(message, HttpStatus.FORBIDDEN);
        }
    }
}
