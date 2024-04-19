package com.example.stockfeed.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  //유저와 연관관계

    private String title;
    private String content;
    private int viewCount;

    @Builder
    public Post(User user, String title, String content, int viewCount) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
