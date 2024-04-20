package com.example.stockfeed.Repository;

import com.example.stockfeed.Domain.Comment;
import com.example.stockfeed.Domain.CommentLike;
import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserAndComment(User user, Comment comment);
}
