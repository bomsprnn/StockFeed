package com.example.stockfeed.Repository;

import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Domain.PostLike;
import com.example.stockfeed.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByUserAndPost(User user, Post post);

    int countByPost(Post post);

    PostLike findByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);
}
