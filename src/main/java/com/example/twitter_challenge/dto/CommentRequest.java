package com.example.twitter_challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Yorum oluşturma ve güncelleme gövdesi: sadece içerik taşır.
 * Hangi tweete yorum yapıldığı URL'den gelir (POST /comment/{tweetId}).
 */
public record CommentRequest(
        @NotBlank(message = "Yorum boş olamaz")
        @Size(max = 280, message = "Yorum en fazla 280 karakter olabilir")
        String content
) {
}
