package com.example.twitter_challenge.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(schema = "public", name = "tweets")
public class Tweet extends EntityBase {

    @NotBlank(message = "Tweet içeriği boş olamaz")
    @Size(max = 280, message = "Tweet en fazla 280 karakter olabilir")
    @Column(name = "content", nullable = false, length = 280)
    private String content;

    // Anonim tweet olmaması için user zorunlu (nullable = false)
    @NotNull(message = "Tweet bir kullanıcıya ait olmalı")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Tweet silinince yorumları, beğenileri ve retweetleri de silinir
    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Retweet> retweets = new ArrayList<>();

    public Tweet(String content, User user) {
        this.content = content;
        this.user = user;
    }
}
