package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.LikeResponse;
import com.example.twitter_challenge.entity.Likes;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.LikeRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private TweetService tweetService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Tweet tweet;

    @BeforeEach
    void setUp() {
        user = TestData.user(2L, "mehmet");
        tweet = TestData.tweet(10L, "Tweet", TestData.user(1L, "aleyna"));
        when(userService.findByUserName("mehmet")).thenReturn(user);
        when(tweetService.getTweetEntity(10L)).thenReturn(tweet);
    }

    @Test
    void like_savesLike() {
        when(likeRepository.existsByUserIdAndTweetId(2L, 10L)).thenReturn(false);

        LikeResponse response = likeService.like(10L, "mehmet");

        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);
        verify(likeRepository).save(any(Likes.class));
    }

    @Test
    void like_twice_throwsConflict() {
        when(likeRepository.existsByUserIdAndTweetId(2L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> likeService.like(10L, "mehmet"))
                .isInstanceOf(TwitterException.class)
                .hasMessageContaining("zaten");
    }

    @Test
    void dislike_removesExistingLike() {
        Likes like = new Likes(user, tweet);
        tweet.getLikes().add(like);
        when(likeRepository.findByUserIdAndTweetId(2L, 10L)).thenReturn(Optional.of(like));

        LikeResponse response = likeService.dislike(10L, "mehmet");

        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isZero();
        verify(likeRepository).delete(like);
    }

    @Test
    void dislike_withoutLike_throwsNotFound() {
        when(likeRepository.findByUserIdAndTweetId(2L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.dislike(10L, "mehmet"))
                .isInstanceOf(TwitterException.class);
    }
}
