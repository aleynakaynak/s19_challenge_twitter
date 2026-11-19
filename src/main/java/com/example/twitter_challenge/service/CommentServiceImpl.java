package com.example.twitter_challenge.service;

import com.example.twitter_challenge.dto.CommentRequest;
import com.example.twitter_challenge.dto.CommentResponse;
import com.example.twitter_challenge.entity.Comment;
import com.example.twitter_challenge.entity.Tweet;
import com.example.twitter_challenge.entity.User;
import com.example.twitter_challenge.exceptions.TwitterException;
import com.example.twitter_challenge.repository.CommentRepository;
import com.example.twitter_challenge.util.DtoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TweetService tweetService;
    private final UserService userService;

    public CommentServiceImpl(CommentRepository commentRepository, TweetService tweetService, UserService userService) {
        this.commentRepository = commentRepository;
        this.tweetService = tweetService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public CommentResponse create(Long tweetId, CommentRequest request, String userName) {
        User user = userService.findByUserName(userName);
        Tweet tweet = tweetService.getTweetEntity(tweetId);
        Comment comment = new Comment(request.content(), user, tweet);
        tweet.getComments().add(comment);
        return DtoConverter.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponse update(Long id, CommentRequest request, String userName) {
        Comment comment = getComment(id);
        if (!comment.getUser().getUserName().equals(userName)) {
            throw new TwitterException("Sadece kendi yorumunuzu güncelleyebilirsiniz", HttpStatus.FORBIDDEN);
        }
        comment.setContent(request.content());
        return DtoConverter.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(Long id, String userName) {
        Comment comment = getComment(id);
        boolean isCommentOwner = comment.getUser().getUserName().equals(userName);
        boolean isTweetOwner = comment.getTweet().getUser().getUserName().equals(userName);
        // README: yorumu sadece tweet sahibi veya yorum sahibi silebilir
        if (!isCommentOwner && !isTweetOwner) {
            throw new TwitterException("Bu yorumu silme yetkiniz yok", HttpStatus.FORBIDDEN);
        }
        comment.getTweet().getComments().remove(comment);
        commentRepository.delete(comment);
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new TwitterException("Yorum bulunamadı: " + id, HttpStatus.NOT_FOUND));
    }
}
