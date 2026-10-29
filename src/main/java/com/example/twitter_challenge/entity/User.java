package com.example.twitter_challenge.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Table(schema = "public", name = "users")
public class User extends EntityBase {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 3, max = 30, message = "Kullanıcı adı 3-30 karakter olmalı")
    @Column(name = "user_name", nullable = false, unique = true, length = 30)
    private String userName;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email giriniz")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Veritabanına her zaman BCrypt ile şifrelenmiş hali yazılır
    @NotBlank(message = "Şifre boş olamaz")
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 160, message = "Bio en fazla 160 karakter olabilir")
    @Column(name = "bio", length = 160)
    private String bio;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tweet> tweets = new ArrayList<>();

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
}
