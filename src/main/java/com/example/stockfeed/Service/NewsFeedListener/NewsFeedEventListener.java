package com.example.stockfeed.Service.NewsFeedListener;

import com.example.stockfeed.Domain.Comment;
import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Repository.NewsFeedRepository;
import com.example.stockfeed.Service.NewsFeedEvent.CommentEvent;
import com.example.stockfeed.Service.NewsFeedEvent.PostEvent;
import com.example.stockfeed.Service.NewsFeedService;
import jakarta.persistence.EntityListeners;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NewsFeedEventListener {

    private final NewsFeedService newsFeedService;
    private final NewsFeedRepository newsFeedRepository;
    @EventListener
    public void handlePostEvent(PostEvent event) {
        if (event.getEventType() == PostEvent.EventType.CREATE) {
            Post post = event.getPost();
            newsFeedService.createPostonNewsFeed(post);
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


}
