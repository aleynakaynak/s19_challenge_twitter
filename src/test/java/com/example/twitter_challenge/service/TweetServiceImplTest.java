package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.TweetRequest;
import com.example.twitter_challenge.dto.TweetResponse;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.TweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TweetServiceImplTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TweetServiceImpl tweetService;

    private User aleyna;
    private User other;
    private Tweet tweet;

    @BeforeEach
    void setUp() {
        aleyna = TestData.user(1L, "aleyna");
        other = TestData.user(2L, "mehmet");
        tweet = TestData.tweet(10L, "Merhaba dünya", aleyna);
    }

    @Test
    void create_savesTweetForLoggedInUser() {
        when(userService.findByUserName("aleyna")).thenReturn(aleyna);
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(inv -> {
            Tweet t = inv.getArgument(0);
            t.setId(99L);
            return t;
        });

        TweetResponse response = tweetService.create(new TweetRequest("İlk tweetim"), "aleyna");

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.content()).isEqualTo("İlk tweetim");
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.userName()).isEqualTo("aleyna");
    }

    @Test
    void findByUserId_returnsUsersTweets() {
        when(userService.findById(1L)).thenReturn(aleyna);
        when(tweetRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(tweet));

        List<TweetResponse> result = tweetService.findByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).content()).isEqualTo("Merhaba dünya");
    }

    @Test
    void findById_throwsNotFound_whenTweetMissing() {
        when(tweetRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tweetService.findById(5L))
                .isInstanceOf(TwitterException.class)
                .extracting(ex -> ((TwitterException) ex).getHttpStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_changesContent_whenOwner() {
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));
        when(tweetRepository.save(tweet)).thenReturn(tweet);

        TweetResponse response = tweetService.update(10L, new TweetRequest("Güncellendi"), "aleyna");

        assertThat(response.content()).isEqualTo("Güncellendi");
    }

    @Test
    void update_throwsForbidden_whenNotOwner() {
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));

        assertThatThrownBy(() -> tweetService.update(10L, new TweetRequest("x"), other.getUserName()))
                .isInstanceOf(TwitterException.class)
                .extracting(ex -> ((TwitterException) ex).getHttpStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
        verify(tweetRepository, never()).save(any());
    }

    @Test
    void delete_removesTweet_whenOwner() {
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));

        tweetService.delete(10L, "aleyna");

        verify(tweetRepository).delete(tweet);
    }

    @Test
    void delete_throwsForbidden_whenNotOwner() {
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));

        assertThatThrownBy(() -> tweetService.delete(10L, "mehmet"))
                .isInstanceOf(TwitterException.class)
                .hasMessageContaining("Sadece tweet sahibi");
        verify(tweetRepository, never()).delete(any());
    }
}
