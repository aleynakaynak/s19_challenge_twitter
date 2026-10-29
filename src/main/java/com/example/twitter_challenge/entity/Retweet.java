package com.example.twitter_challenge.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Twitter'daki gibi: bir kullanıcı bir tweeti bir kez retweet edebilir,
 * retweeti geri aldığında kayıt silinir.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(schema = "public", name = "retweets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tweet_id"}))
public class Retweet extends EntityBase {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    public Retweet(User user, Tweet tweet) {
        this.user = user;
        this.tweet = tweet;
    }
}
