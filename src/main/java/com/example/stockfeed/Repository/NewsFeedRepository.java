package com.example.stockfeed.Repository;

import com.example.stockfeed.Domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NewsFeedRepository extends JpaRepository<NewsFeed, Long> {
    // 게시글에 해당하는 뉴스피드 삭제
    void deleteByPost(Post post);
    // 댓글에 해당하는 뉴스피드 삭제
    void deleteByComment(Comment comment);
    // 팔로워의 뉴스피드에서 언팔로우한 유저의 활동 삭제
    void deleteByUserAndOwnUser(User user, User ownUser);
    // 게시글 좋아요취소 시 해당하는 뉴스피드 삭제
    void deleteByPostAndType(Post post, NewsFeedType type);
    // 댓글 좋아요취소 시 해당하는 뉴스피드 삭제
    void deleteByCommentAndType(Comment comment, NewsFeedType type);
}

