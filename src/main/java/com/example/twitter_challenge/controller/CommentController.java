package com.example.twitter_challenge.controller;

import com.example.twitter_challenge.dto.CommentRequest;
import com.example.twitter_challenge.dto.CommentResponse;
import com.example.twitter_challenge.dto.CommentUpdateRequest;
import com.example.twitter_challenge.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(@Valid @RequestBody CommentRequest request, Authentication authentication) {
        return commentService.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public CommentResponse update(@PathVariable Long id,
                                  @Valid @RequestBody CommentUpdateRequest request,
                                  Authentication authentication) {
        return commentService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        commentService.delete(id, authentication.getName());
    }
}
