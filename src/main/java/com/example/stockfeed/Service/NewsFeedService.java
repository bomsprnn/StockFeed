package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.*;
import com.example.stockfeed.Repository.NewsFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsFeedService {
    private final NewsFeedRepository newsFeedRepository;


    // 범용 NewsFeed 생성 및 저장 메서드
    private void createAndSaveNewsFeed(User targetUser, User ownUser,
                                       Post post, Comment comment, NewsFeedType type) {
        NewsFeed newsFeed = NewsFeed.builder()
                .user(targetUser)
                .ownUser(ownUser)
                .post(post)
                .comment(comment)
                .type(type)
                .build();
        newsFeedRepository.save(newsFeed);
    }

    // 팔로우한 유저의 뉴스피드에 게시글 생성
    public void createPostonNewsFeed(Post post) {
        List<User> followers = getFollowers(post.getUser());
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, post.getUser(), post, null, NewsFeedType.POST));
    }


    // 팔로우한 유저의 뉴스피드에 댓글 생성
    public void createCommentonNewsFeed(Comment comment) {
        List<User> followers = getFollowers(comment.getUser());
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, comment.getUser(), null, comment, NewsFeedType.COMMENT));
    }

    // 팔로우한 유저의 뉴스피드에 팔로우 생성
    public void createFollowonNewsFeed(User user, User ownUser) { //user가 ownUser를 팔로우함
        List<User> followers = getFollowers(user); //user를 팔로우하는 사람들
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, ownUser, null, null, NewsFeedType.FOLLOW));
    }

    // 팔로우한 유저의 뉴스피드에 게시글 좋아요 생성
    public void createLikeonNewsFeed(PostLike postLike) {
        List<User> followers = getFollowers(postLike.getUser());
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, postLike.getUser(), postLike.getPost(), null, NewsFeedType.POSTLIKE));
    }

    // 팔로우한 유저의 뉴스피드에 댓글 좋아요 생성
    public void createLikeonNewsFeed(CommentLike commentLike) {
        List<User> followers = getFollowers(commentLike.getUser());
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, commentLike.getUser(), null, commentLike.getComment(), NewsFeedType.COMMENTLIKE));
    }

    // 글에 댓글이 달렸을 때 글의 주인의 뉴스피드에 생성
    public void createCommentonOwnNewsFeed(Comment comment) {
        createAndSaveNewsFeed(
                comment.getPost().getUser(), comment.getUser(), null, comment, NewsFeedType.COMMENT);
    }

    // 글에 좋아요가 눌렸을 때 글의 주인의 뉴스피드에 생성
    public void createLikeonOwnNewsFeed(PostLike postLike) {
        createAndSaveNewsFeed(
                postLike.getPost().getUser(), postLike.getUser(), postLike.getPost(), null, NewsFeedType.POSTLIKE);
    }

    // 댓글에 좋아요가 눌렸을 때 댓글의 주인의 뉴스피드에 생성
    public void createLikeonOwnNewsFeed(CommentLike commentLike) {
        createAndSaveNewsFeed(
                commentLike.getComment().getUser(), commentLike.getUser(), null, commentLike.getComment(), NewsFeedType.COMMENTLIKE);
    }


    // 팔로우 목록 조회
    private List<User> getFollowers(User user) {
        List<User> followers = user.getFollowers().stream()
                .map(Follow::getFollower)
                .toList();
        return followers;
    }

}
