package com.example.stockfeed.Domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {

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

    private LocalDateTime lastLogoutAt;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private List<Follow> followings = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, String profileImage, String profileText, UserRole role,List<Follow> followers, List<Follow> followings) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImage = profileImage;
        this.profileText = profileText;
        this.role = role;

        this.followers = followers;
        this.followings = followings;

        // posts, comments, likes, followers, followings 초기화는 생략
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.role != null) {
            authorities.add(new SimpleGrantedAuthority(this.role.name()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateProfileImage(String imagePath) {
        this.profileImage = imagePath;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateProfileText(String profileText) {
        this.profileText = profileText;
    }

    public void updatePassword(String password) {
        this.password = password; // 실제 사용 시에는 암호화 처리 필요
    }
}
