package com.example.stockfeed.Service;

import com.example.stockfeed.Config.RedisUtil;
import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Domain.UserRole;
import com.example.stockfeed.Dto.SignUpDto;
import com.example.stockfeed.Dto.UserUpdateDto;
import com.example.stockfeed.Repository.UserRepository;
import com.example.stockfeed.Service.JWT.JwtProvider;
import com.example.stockfeed.Service.JWT.JwtToken;
import com.example.stockfeed.Service.JWT.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final EmailAuthService emailAuthService;
    private final RedisUtil redisUtil;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    @Value("${file.storage-path}")
    private String imageStoragePath;


    // 회원가입 시 유효성 검사 + Redis에 저장
    // dto 이메일, 비밀번호, 이름, 프로필 이미지, 인사말
    public void preSignUp(SignUpDto signUpDto, MultipartFile file) {
        checkavailable(signUpDto);
        String imagePath = null;
        if (file != null && !file.isEmpty()) {
            imagePath = saveProfileImage(file); // 이미지 파일 저장 메소드
        }
        signUpDto.setProfileImage(imagePath); // 이미지 파일 경로 dto에 저장

        String key = "REGIST:" + signUpDto.getEmail();
        String value = null;
        try { // 회원가입 정보를 Redis에 저장
            value = objectMapper.writeValueAsString(signUpDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisUtil.setDataExpire(key, value, 60 * 6L);
        // 회원 정보 6분동안 저장 (이메일 인증 duration이 5분이므로 6분으로 설정)
    }

    // 회원가입
    public boolean signUpConfirm(int authNumber) {
        SignUpDto signUpDto = emailAuthService.confirmSignUp(authNumber);
        if (signUpDto == null) {
            return false;
        }
        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .profileImage(signUpDto.getProfileImage())
                .profileText(signUpDto.getProfileText())
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
        userRepository.save(user);

        return true;
    }


    // 로그인 ( JWT 토큰 생성 )
    public JwtToken login(String email, String password) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        log.info("로그인 정보" + authenticationToken.toString() + "이름 " + authenticationToken.getName());
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        // 2. authenticate 메서드가 실행될 때 CustomUserDetailsService의 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("인증 객체 정보" + authenticationManagerBuilder.getObject().toString() + "이름 " + authentication.getName());

        // 3. 인증 객체를 기반으로 JWT 토큰 생성
        return jwtProvider.generateToken(authentication);

    }

    // 로그아웃
    public int logout() {
        String email = getCurrentUser();
        long now = System.currentTimeMillis();
        redisService.saveLastLogout(email, now);
        return 1;
    }


    // 회원 정보 수정 폼을 위한 회원 정보 불러오기
    public SignUpDto getUserInfo(String password) {
        String email = getCurrentUser();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        checkPassword(password); // 현재 비밀번호 확인 (비밀번호가 일치하지 않으면 예외 발생
        return SignUpDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .profileText(user.getProfileText())
                .build();
    }

    // 회원 정보 수정
    public void updateUser(UserUpdateDto updatedInfo, MultipartFile file) {
        String email = getCurrentUser();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 프로필 이미지 업데이트
        if (file != null && !file.isEmpty()) {
            String imagePath = saveProfileImage(file); // 이미지 파일 저장 메소드
            user.setProfileImage(imagePath);
        }
        // 이름 업데이트
        if (!updatedInfo.getName().isEmpty()) {
            user.setName(updatedInfo.getName());
        }
        // 프로필 멘트 업데이트
        if (!updatedInfo.getProfileText().isEmpty()) {
            user.setProfileText(updatedInfo.getProfileText());
        }
        // 비밀번호 업데이트
        if (!updatedInfo.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedInfo.getPassword()));
        }

        userRepository.save(user);
    }


    // 현재 비밀번호 확인
    public boolean checkPassword(String password) {
        String email = getCurrentUser();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        return passwordEncoder.matches(password, user.getPassword());
    }

    // 프로필 이미지 저장
    private String saveProfileImage(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path storagePath = Paths.get(imageStoragePath + fileName);
            Files.copy(file.getInputStream(), storagePath, StandardCopyOption.REPLACE_EXISTING);
            return storagePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("사진 저장에 실패하였습니다.", e);
        }
    }


    // 회원가입 시 유효성 검사
    private void checkavailable(SignUpDto signUpDto) { // 유효성 검사
        if (signUpDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해야 합니다.");
        }
        if (signUpDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해야 합니다.");
        }
        if (!signUpDto.getEmail().contains("@")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        if (signUpDto.getName().isEmpty()) {
            throw new IllegalArgumentException("이름을 입력해야 합니다.");
        }

        if (signUpDto.getProfileText().isEmpty()) {
            throw new IllegalArgumentException("인사말을 작성해야 합니다.");
        }
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }

    // 현재 사용자 정보 반환
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            log.info("현재 사용자: {}", ((UserDetails) authentication.getPrincipal()).getUsername());
            log.info("현재 사용자 role: {}", ((UserDetails) authentication.getPrincipal()).getAuthorities());

            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            throw new IllegalStateException("인증된 사용자를 찾을 수 없습니다.");
        }
    }


    // 유저 이름으로 유저 정보 반환
    public User getUser(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
