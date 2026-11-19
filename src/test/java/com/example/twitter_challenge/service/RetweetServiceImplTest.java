package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.RetweetResponse;
import com.example.twitter_challenge.entity.Retweet;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.RetweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetweetServiceImplTest {

    @Mock
    private RetweetRepository retweetRepository;

    @Mock
    private TweetService tweetService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RetweetServiceImpl retweetService;

    private User user;
    private Tweet tweet;

    @BeforeEach
    void setUp() {
        user = TestData.user(2L, "mehmet");
        tweet = TestData.tweet(10L, "Tweet", TestData.user(1L, "aleyna"));
    }

    @Test
    void retweet_savesRetweet() {
        when(userService.findByUserName("mehmet")).thenReturn(user);
        when(tweetService.getTweetEntity(10L)).thenReturn(tweet);
        when(retweetRepository.existsByUserIdAndTweetId(2L, 10L)).thenReturn(false);
        when(retweetRepository.save(any(Retweet.class))).thenAnswer(inv -> {
            Retweet r = inv.getArgument(0);
            r.setId(7L);
            return r;
        });

        RetweetResponse response = retweetService.retweet(10L, "mehmet");

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.userName()).isEqualTo("mehmet");
        assertThat(tweet.getRetweets()).hasSize(1);
    }

    @Test
    void retweet_twice_throwsConflict() {
        when(userService.findByUserName("mehmet")).thenReturn(user);
        when(tweetService.getTweetEntity(10L)).thenReturn(tweet);
        when(retweetRepository.existsByUserIdAndTweetId(2L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> retweetService.retweet(10L, "mehmet"))
                .isInstanceOf(TwitterException.class);
        verify(retweetRepository, never()).save(any());
    }

    @Test
    void delete_ownRetweet() {
        Retweet retweet = new Retweet(user, tweet);
        retweet.setId(7L);
        tweet.getRetweets().add(retweet);
        when(retweetRepository.findById(7L)).thenReturn(Optional.of(retweet));

        retweetService.delete(7L, "mehmet");

        verify(retweetRepository).delete(retweet);
        assertThat(tweet.getRetweets()).isEmpty();
    }

    @Test
    void delete_othersRetweet_throwsForbidden() {
        Retweet retweet = new Retweet(user, tweet);
        when(retweetRepository.findById(7L)).thenReturn(Optional.of(retweet));

        assertThatThrownBy(() -> retweetService.delete(7L, "aleyna"))
                .isInstanceOf(TwitterException.class);
    }
}
