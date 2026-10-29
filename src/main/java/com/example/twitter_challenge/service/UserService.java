package com.example.twitter_challenge.service;

import com.example.twitter_challenge.entity.User;

public interface UserService {

    User findByUserName(String userName);

    User findById(Long id);
}
