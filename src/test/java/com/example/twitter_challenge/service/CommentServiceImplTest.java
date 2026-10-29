package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.CommentRequest;
import com.example.twitter_challenge.dto.CommentResponse;
import com.example.twitter_challenge.dto.CommentUpdateRequest;
import com.example.twitter_challenge.entity.Comment;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.CommentRepository;
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
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TweetService tweetService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User tweetOwner;
    private User commenter;
    private User stranger;
    private Tweet tweet;
    private Comment comment;

    @BeforeEach
    void setUp() {
        tweetOwner = TestData.user(1L, "aleyna");
        commenter = TestData.user(2L, "mehmet");
        stranger = TestData.user(3L, "ayse");
        tweet = TestData.tweet(10L, "Tweet", tweetOwner);
        comment = new Comment("Güzel tweet", commenter, tweet);
        comment.setId(50L);
        tweet.getComments().add(comment);
    }

    @Test
    void create_addsCommentToTweet() {
        when(userService.findByUserName("ayse")).thenReturn(stranger);
        when(tweetService.getTweetEntity(10L)).thenReturn(tweet);
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        CommentResponse response = commentService.create(new CommentRequest(10L, "Katılıyorum"), "ayse");

        assertThat(response.content()).isEqualTo("Katılıyorum");
        assertThat(response.userName()).isEqualTo("ayse");
        assertThat(tweet.getComments()).hasSize(2);
    }

    @Test
    void update_onlyCommentOwnerCanUpdate() {
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentResponse response = commentService.update(50L, new CommentUpdateRequest("Düzenlendi"), "mehmet");

        assertThat(response.content()).isEqualTo("Düzenlendi");
    }

    @Test
    void update_throwsForbidden_forOtherUser() {
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.update(50L, new CommentUpdateRequest("x"), "aleyna"))
                .isInstanceOf(TwitterException.class);
    }

    @Test
    void delete_allowedForCommentOwner() {
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));

        commentService.delete(50L, "mehmet");

        verify(commentRepository).delete(comment);
    }

    @Test
    void delete_allowedForTweetOwner() {
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));

        commentService.delete(50L, "aleyna");

        verify(commentRepository).delete(comment);
        assertThat(tweet.getComments()).isEmpty();
    }

    @Test
    void delete_forbiddenForStranger() {
        when(commentRepository.findById(50L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.delete(50L, "ayse"))
                .isInstanceOf(TwitterException.class)
                .hasMessageContaining("yetkiniz yok");
        verify(commentRepository, never()).delete(any());
    }
}
