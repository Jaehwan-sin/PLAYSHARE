package com.tech.spotify.service;

import com.tech.spotify.Repository.UserRepository;
import com.tech.spotify.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Long join(User user) {
        // 중복 회원 검증
        validateDuplicateUser(user);

        userRepository.save(user);
        return user.getId();
    }

    // 중복 회원 검증 메서드
    private void validateDuplicateUser(User user) {
        User findUser = userRepository.findByEmail(user.getEmail());

        if (findUser != null) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    public List<User> findUsers() {
        return userRepository.findAll();
    }

    public User findOneById(Long id) {
        return userRepository.findOneById(id);
    }

    public void validateLogin(String email, String password, HttpSession session) {
        User user = userRepository.findByEmail(email);

        // 사용자가 존재하고 비밀번호도 일치하는지 확인
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 로그인 성공
            System.out.println("로그인 성공");
            session.setAttribute("user", user);
        } else {
            // 로그인 실패
            System.out.println("로그인 실패");
            throw new IllegalArgumentException("로그인 실패");
        }
    }
}
