package com.example.twitter_challenge.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void twitterException_keepsStatusAndMessage() {
        ResponseEntity<ErrorResponse> response =
                handler.handleTwitterException(new TwitterException("Tweet bulunamadı: 5", HttpStatus.NOT_FOUND));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Tweet bulunamadı: 5");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    @Test
    void authenticationException_returns401() {
        ResponseEntity<ErrorResponse> response = handler.handleAuthentication(new BadCredentialsException("x"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void unknownException_returns500WithoutLeakingDetails() {
        ResponseEntity<ErrorResponse> response = handler.handleAll(new IllegalStateException("gizli detay"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).doesNotContain("gizli detay");
    }
}
