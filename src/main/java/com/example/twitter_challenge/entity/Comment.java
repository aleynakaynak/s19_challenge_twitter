package com.example.twitter_challenge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(schema = "public", name = "comments")
public class Comment extends EntityBase {

    @NotBlank(message = "Yorum boş olamaz")
    @Size(max = 280, message = "Yorum en fazla 280 karakter olabilir")
    @Column(name = "content", nullable = false, length = 280)
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    public Comment(String content, User user, Tweet tweet) {
        this.content = content;
        this.user = user;
        this.tweet = tweet;
    }
}
