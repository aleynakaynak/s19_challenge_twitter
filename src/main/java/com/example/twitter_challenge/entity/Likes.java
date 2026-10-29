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
 * Hocanın iskeletindeki "teknik borç" notu tamamlandı:
 * tweet_id ve user_id artık gerçek ManyToOne ilişkileri.
 * Bir kullanıcı bir tweeti yalnızca bir kez beğenebilir (unique constraint).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(schema = "public", name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tweet_id"}))
public class Likes extends EntityBase {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    public Likes(User user, Tweet tweet) {
        this.user = user;
        this.tweet = tweet;
    }
}
