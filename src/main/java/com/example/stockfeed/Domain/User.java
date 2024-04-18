package com.example.stockfeed.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    private Long id; // 유저 고유번호

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
    private String name;
    private String profileImage;
    private String profileText;
    private String role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private List<Follow> followings = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, String profileImage, String profileText, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
    }


}
