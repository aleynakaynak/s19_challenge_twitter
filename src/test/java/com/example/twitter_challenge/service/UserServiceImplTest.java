package com.example.twitter_challenge.service;

import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void loadUserByUsername_returnsSpringUser() {
        User user = TestData.user(1L, "aleyna");
        when(userRepository.findByUserName("aleyna")).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("aleyna");

        assertThat(details.getUsername()).isEqualTo("aleyna");
        assertThat(details.getPassword()).isEqualTo("encoded-password");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_unknownUser_throws() {
        when(userRepository.findByUserName("yok")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("yok"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void findById_unknownUser_throwsTwitterException() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(9L))
                .isInstanceOf(TwitterException.class);
    }
}
