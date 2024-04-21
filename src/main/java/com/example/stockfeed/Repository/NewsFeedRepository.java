package com.example.stockfeed.Repository;

import com.example.stockfeed.Domain.NewsFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsFeedRepository extends JpaRepository<NewsFeed, Long> {
}
