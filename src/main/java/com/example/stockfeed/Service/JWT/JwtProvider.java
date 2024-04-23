package com.example.stockfeed.Service.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;
    private final RedisService redisService;
    private CustomUserDetailsService userDetailsService;


    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtProvider(@Value("${jwt.secret}") String secretKey, RedisService redisService, CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisService = redisService;
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authority = authentication.getAuthorities().iterator().next().getAuthority();


        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 3600000); // 1시간
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authority)
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(now + 86400000 * 7)) // 7일
                .setIssuedAt(new Date(now))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        redisService.saveRefreshToken(authentication.getName(), refreshToken, 86400000);
        // redis에 리프레시 토큰 저장

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //로그아웃
    public void logout(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims == null) {
            throw new RuntimeException("파싱 실패");
        }
        String username = claims.getSubject();
        Date expiration = claims.getExpiration();
        long remainingTime = expiration.getTime() - System.currentTimeMillis();
        //토큰 잔여시간 저장
        if (remainingTime > 0) {
            redisService.saveBlacklist(accessToken, remainingTime);
        } // 토큰 잔여시간이 0보다 크면 블랙리스트에 저장(이를 블랙리스트에 올라가있는 시간으로)
    }


    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get("auth").toString());


        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject(), "", Collections.singletonList(authority));
        return new UsernamePasswordAuthenticationToken(principal, "", Collections.singletonList(authority));
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            if (redisService.isBlacklisted(token)) {
                log.info("블랙리스트에 포함된 토큰입니다.");
                return false;
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            long issuedAt = claims.getIssuedAt().getTime();
            // Redis에서 사용자의 lastLogout timestamp 가져오기
            Long lastLogout = redisService.getLastLogout(username);
            if (lastLogout != null && issuedAt < lastLogout) {
                log.info("Token issued before the last logout. Invalid token.");
                return false;
            }
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        } catch (Exception e) {
            log.info("Token validation error", e);
        }
        return false;
    }


    // 리프레시 토큰으로 액세스 토큰 재발급
    public JwtToken refreshAccessToken(String refreshToken) {
        // 리프레시 토큰의 유효성 검사
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }
        String username = getUsernameFromToken(refreshToken);

        // Redis에서 사용자의 lastLogout timestamp 가져오기
        Long lastLogout = redisService.getLastLogout(username);
        if (lastLogout != null) {
            // 리프레시 토큰의 발급 시간 확인
            Claims claims = parseClaims(refreshToken);
            long issuedAt = claims.getIssuedAt().getTime();
            // 마지막 로그아웃 시간이 토큰 발급 시간보다 뒤인 경우, 토큰 발급 거부
            if (issuedAt < lastLogout) {
                throw new RuntimeException("마지막 로그아웃 이후에 발급된 리프레시 토큰이 아닙니다.");
            }
        }

        // 리프레시 토큰에서 사용자 이름 추출
        String savedRefreshToken = redisService.getRefreshToken(username);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 사용자 이름으로부터 UserDetails 가져오기
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // UserDetails를 이용하여 Authentication 객체 생성
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 새 액세스 토큰 생성
        return generateToken(newAuthentication);
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    // accessToken
    private Claims parseClaims(String accessToken) {
        try {

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
