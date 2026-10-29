package com.example.twitter_challenge.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Kullanıcı adı boş olamaz")
        String userName,

        @NotBlank(message = "Şifre boş olamaz")
        String password
) {
}
