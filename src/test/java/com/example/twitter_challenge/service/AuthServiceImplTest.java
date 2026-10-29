package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.LoginRequest;
import com.example.twitter_challenge.dto.RegisterRequest;
import com.example.twitter_challenge.dto.UserResponse;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_encodesPasswordAndSaves() {
        when(userRepository.existsByUserName("aleyna")).thenReturn(false);
        when(userRepository.existsByEmail("aleyna@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse response = authService.register(new RegisterRequest("aleyna", "aleyna@test.com", "123456"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed");
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userName()).isEqualTo("aleyna");
    }

    @Test
    void register_duplicateUserName_throwsConflict() {
        when(userRepository.existsByUserName("aleyna")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("aleyna", "a@test.com", "123456")))
                .isInstanceOf(TwitterException.class)
                .hasMessageContaining("zaten alınmış");
    }

    @Test
    void login_returnsUser_whenCredentialsValid() {
        User user = TestData.user(1L, "aleyna");
        when(userRepository.findByUserName("aleyna")).thenReturn(Optional.of(user));

        UserResponse response = authService.login(new LoginRequest("aleyna", "123456"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.userName()).isEqualTo("aleyna");
    }

    @Test
    void login_wrongPassword_throwsBadCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("aleyna", "yanlis")))
                .isInstanceOf(BadCredentialsException.class);
    }
}
