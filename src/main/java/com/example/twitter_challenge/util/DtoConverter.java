package com.example.twitter_challenge.util;

import com.example.twitter_challenge.dto.CommentResponse;
import com.example.twitter_challenge.dto.RetweetResponse;
import com.example.twitter_challenge.dto.TweetResponse;
import com.example.twitter_challenge.dto.UserResponse;
import com.example.twitter_challenge.entity.Comment;
import com.example.twitter_challenge.entity.Retweet;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;

/**
 * Entity'leri doğrudan dönmek yerine DTO dönüyoruz:
 * şifre gibi alanlar dışarı çıkmaz ve çift yönlü ilişkilerde
 * sonsuz JSON döngüsü oluşmaz.
 */
public final class DtoConverter {

    private DtoConverter() {
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUserName(), user.getEmail());
    }

    public static TweetResponse toTweetResponse(Tweet tweet) {
        return new TweetResponse(
                tweet.getId(),
                tweet.getContent(),
                tweet.getUser().getId(),
                tweet.getUser().getUserName(),
                tweet.getLikes().size(),
                tweet.getComments().size(),
                tweet.getRetweets().size(),
                tweet.getCreatedAt(),
                tweet.getUpdatedAt()
        );
    }

    public static CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getTweet().getId(),
                comment.getUser().getId(),
                comment.getUser().getUserName(),
                comment.getCreatedAt()
        );
    }

    public static RetweetResponse toRetweetResponse(Retweet retweet) {
        return new RetweetResponse(
                retweet.getId(),
                retweet.getTweet().getId(),
                retweet.getUser().getId(),
                retweet.getUser().getUserName(),
                retweet.getCreatedAt()
        );
    }
}
