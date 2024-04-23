package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.*;
import com.example.stockfeed.Dto.NewsFeedDto;
import com.example.stockfeed.Repository.NewsFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NewsFeedService {
    private final NewsFeedRepository newsFeedRepository;

    public List<NewsFeedDto> getNewsFeedsWithCursor(Long userId, Long cursorId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        List<NewsFeed> newsFeeds = newsFeedRepository.findNewsFeedsByUserBeforeCursor(userId, cursorId, pageRequest);
        log.info("뉴스피드 조회 완료"+newsFeeds.size());
        return newsFeeds.stream().map(newsFeed -> new NewsFeedDto(
                newsFeed.getId(),
                newsFeed.getUser().getId(),
                newsFeed.getOwnUser().getId(),
                newsFeed.getType().toString(),
                (newsFeed.getFollowUser() != null) ? newsFeed.getFollowUser().getId() : null,
                (newsFeed.getPost() != null) ? newsFeed.getPost().getId() : null,
                (newsFeed.getComment() != null) ? newsFeed.getComment().getId() : null
        )).collect(Collectors.toList());
    }


    // 범용 NewsFeed 생성 및 저장 메서드
    private void createAndSaveNewsFeed(User targetUser, User ownUser,
                                       Post post, Comment comment, NewsFeedType type, User followUser) {
        boolean exists = false;
        if (post != null) {
            exists = newsFeedRepository.existsByUserAndPostAndType(targetUser, post, type);
        } else if (comment != null) {
            exists = newsFeedRepository.existsByUserAndCommentAndType(targetUser, comment, type);
        }
        if (exists) { // 원작자가 팔로워인 경우! 뉴스피드에 중복 생성되는 것을 방지
            log.info("이미 생성된 뉴스피드 항목이 있어 중복 생성 되지 않았습니다.");
            return;
        }
        NewsFeed newsFeed = NewsFeed.builder()
                .user(targetUser)
                .ownUser(ownUser)
                .post(post)
                .comment(comment)
                .type(type)
                .followUser(followUser)
                .build();
        newsFeedRepository.save(newsFeed);
    }

    // 팔로우한 유저의 뉴스피드에 게시글 생성
    public void createPostonNewsFeed(Post post) {
        List<User> followers = getFollowers(post.getUser());
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, post.getUser(), post, null, NewsFeedType.POST, null));

    }


    // 팔로우한 유저의 뉴스피드에 댓글 생성
    public void createCommentonNewsFeed(Comment comment) {
        List<User> followers = getFollowers(comment.getUser());
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, comment.getUser(), null, comment, NewsFeedType.COMMENT, null));
        createAndSaveNewsFeed( // 원작자의 뉴스피드에도 생성
                comment.getPost().getUser(), comment.getUser(), null, comment, NewsFeedType.COMMENT, null);
    }

    // 팔로우한 유저의 뉴스피드에 팔로우 생성
    public void createFollowonNewsFeed(User follower, User followee) { //follower가 followee를 팔로우함
        List<User> followersofA = getFollowers(follower); //user를 팔로우하는 사람들
        log.info("팔로우하는사람의팔로워 : " + followersofA.get(0).getUsername() + " " + followersofA.get(1).getUsername());
        log.info("팔로우하는사람 : " + follower.getUsername());
        log.info("팔로우당한사람 : " + followee.getUsername());
        log.info(follower.getFollowers().toString());

        followersofA.forEach(followerofA -> createAndSaveNewsFeed(
                followerofA, follower, null, null, NewsFeedType.FOLLOW, followee));
    }

    // 팔로우한 유저의 뉴스피드에 게시글 좋아요 생성
    public void createPostLikeonNewsFeed(PostLike postLike) {
        List<User> followers = getFollowers(postLike.getUser());
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, postLike.getUser(), postLike.getPost(), null, NewsFeedType.POSTLIKE, null));
        createAndSaveNewsFeed( // 원작자의 뉴스피드에도 생성
                postLike.getPost().getUser(), postLike.getUser(), postLike.getPost(), null, NewsFeedType.POSTLIKE, null);
    }

    // 팔로우한 유저의 뉴스피드에 댓글 좋아요 생성
    public void createCommentLikeonNewsFeed(CommentLike commentLike) {
        List<User> followers = getFollowers(commentLike.getUser());
        followers.forEach(follower -> createAndSaveNewsFeed(
                follower, commentLike.getUser(), null, commentLike.getComment(), NewsFeedType.COMMENTLIKE, null));
        createAndSaveNewsFeed( // 원작자의 뉴스피드에도 생성
                commentLike.getComment().getUser(), commentLike.getUser(), null, commentLike.getComment(), NewsFeedType.COMMENTLIKE, null);
    }

    // 팔로우 목록 조회
    private List<User> getFollowers(User user) {
        List<User> followers = user.getFollowers().stream()
                .map(Follow::getFollower)
                .toList();


        return followers;
    }

}
