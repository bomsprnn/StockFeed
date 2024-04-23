package com.example.stockfeed.Service.NewsFeedListener;

import com.example.stockfeed.Domain.*;
import com.example.stockfeed.Repository.NewsFeedRepository;
import com.example.stockfeed.Service.NewsFeedEvent.*;
import com.example.stockfeed.Service.NewsFeedService;
import jakarta.persistence.EntityListeners;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NewsFeedEventListener {

    private final NewsFeedService newsFeedService;
    private final NewsFeedRepository newsFeedRepository;

    @EventListener
    public void handlePostEvent(PostEvent event) {
        if (event.getEventType() == PostEvent.EventType.CREATE) {
            Post post = event.getPost();
            newsFeedService.createPostonNewsFeed(post);
            log.info("PostEvent CREATE " + post.getTitle());
            // 게시물 생성 이벤트 처리
        } else if (event.getEventType() == PostEvent.EventType.DELETE) {
            Post post = event.getPost();
            newsFeedRepository.deleteByPost(post);
            // 게시물 삭제 이벤트 처리
        }
    }

    @EventListener
    public void handleCommentEvent(CommentEvent event) {
        if (event.getEventType() == CommentEvent.EventType.CREATE) {
            Comment comment = event.getComment();
            newsFeedService.createCommentonNewsFeed(comment);
            // 댓글 생성 이벤트 처리
        } else if (event.getEventType() == CommentEvent.EventType.DELETE) {
            Comment comment = event.getComment();
            newsFeedRepository.deleteByComment(comment);
            // 댓글 삭제 이벤트 처리
        }
    }

    @EventListener
    public void handleFollowEvent(FollowEvent event) {
        if (event.getEventType() == FollowEvent.EventType.FOLLOW) {
            Follow follow = event.getFollow();
            newsFeedService.createFollowonNewsFeed(follow.getFollower(), follow.getFollowing());
            // 팔로우 생성 이벤트 처리
        } else if (event.getEventType() == FollowEvent.EventType.UNFOLLOW) {
            Long followerId = event.getFollowerId();
            Long followeeId = event.getFolloweeId();
            newsFeedRepository.deleteByOwnUserIdAndFollowUserId(followerId, followeeId);
            // 팔로우 삭제 이벤트 처리
        }
    }

    @EventListener
    public void handleCommentLikeEvent(CommentLikeEvent event) {
        CommentLike commentLike = event.getCommentLike();
        newsFeedService.createCommentLikeonNewsFeed(commentLike);
    }

    @EventListener
    public void handlePostLikeEvent(PostLikeEvent event) { //포스트 좋아요 삭제
        if (event.getEventType() == PostLikeEvent.EventType.CREATE) {
            newsFeedService.createPostLikeonNewsFeed(event.getPostLike());
        } else if (event.getEventType() == PostLikeEvent.EventType.DELETE) {
            log.info("PostLikeEvent DELETE 호출 완료. 이거 찍히면 repository 수정");
            newsFeedRepository.deleteByOwnUserIdAndPostIdAndType(event.getUser().getId(), event.getPost().getId(), NewsFeedType.POSTLIKE);
        }
    }


}
