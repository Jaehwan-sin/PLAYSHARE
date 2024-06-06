package com.tech.spotify.controller;

import com.tech.jwt.JwtAuthenticationFilter;
import com.tech.jwt.JwtProperties;
import com.tech.spotify.Repository.UserRepository;
import com.tech.spotify.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final UserRepository userRepository;

    private boolean isAuthenticated(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("HomeController authentication = " + authentication);

        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            System.out.println("HomeController principal = " + principal);

            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                System.out.println("HomeController oAuth2User = " + oAuth2User);
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("userId", oAuth2User.getAttribute("name"));
                System.out.println("oAuth2User ID = " + oAuth2User);
                return true;
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("userId", userDetails.getUsername());
                System.out.println("UserDetails ID = " + userDetails.getUsername());
                return true;
            } else {
                // Handle other types of principal
                System.out.println("Unknown principal type: " + principal.getClass());
            }
        }
        return false;
    }

    @GetMapping("/")
    public String check() {
        return "ok";
    }

    @GetMapping("/api/v1/user")
    public String user() {
        return "user";
    }

    @GetMapping("/main")
    public String main(Model model, HttpSession session) {
        if (isAuthenticated(model)) {
            return "main";
        }

        return "main";
    }

    @GetMapping("/user/logout")
    public String logout(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user != null) {
            // 세션에서 사용자 정보를 제거하여 로그아웃 처리
            session.removeAttribute("user");

            // 로그아웃된 사용자 정보를 출력
            log.info("Logged out user: {}", user.getUsername());
        }

        return "main";
    }

}

