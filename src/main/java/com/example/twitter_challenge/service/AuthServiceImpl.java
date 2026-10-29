package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.LoginRequest;
import com.example.twitter_challenge.dto.RegisterRequest;
import com.example.twitter_challenge.dto.UserResponse;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.UserRepository;
import com.example.twitter_challenge.util.DtoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUserName(request.userName())) {
            throw new TwitterException("Bu kullanıcı adı zaten alınmış: " + request.userName(), HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new TwitterException("Bu email ile kayıtlı bir kullanıcı var: " + request.email(), HttpStatus.CONFLICT);
        }
        User user = new User(request.userName(), request.email(), passwordEncoder.encode(request.password()));
        return DtoConverter.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        // Şifre yanlışsa AuthenticationManager BadCredentialsException fırlatır -> 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userName(), request.password()));
        User user = userRepository.findByUserName(request.userName())
                .orElseThrow(() -> new TwitterException("Kullanıcı bulunamadı", HttpStatus.UNAUTHORIZED));
        return DtoConverter.toUserResponse(user);
    }
}
