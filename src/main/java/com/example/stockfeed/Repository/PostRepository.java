package com.example.stockfeed.Repository;

import com.example.stockfeed.Domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    // 날짜 기준으로 내림차순 정렬하여 페이징 처리
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    // 유저 아이디로 검색하여 날짜 기준으로 내림차순 정렬하여 페이징 처리

}
