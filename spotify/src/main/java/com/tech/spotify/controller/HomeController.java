package com.tech.spotify.controller;

import com.tech.spotify.Repository.UserRepository;
import com.tech.spotify.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final UserRepository userRepository;

    @RequestMapping("/main")
    public String main(Model model, HttpSession session) {

        User user = (User) session.getAttribute("user");
        System.out.println("user (homecontroller) = " + user);

        if (user != null) {
            // 사용자의 id 사용
            Long userId = user.getId();

            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userId", userId);
            System.out.println("user id = " + userId);
            System.out.println("session.getId() = " + session.getId());
            System.out.println("model : "+ model.getAttribute("isLoggedIn"));
        } else if (user == null) {
            model.addAttribute("isLoggedIn", false);
            System.out.println("User not logged in");
            System.out.println("model : "+ model.getAttribute("isLoggedIn"));
        }

        log.info("Home controller check");
        return "main";
    }

    @GetMapping("/user/logout")
    public String logout(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user != null) {
            // 세션에서 사용자 정보를 제거하여 로그아웃 처리
            session.removeAttribute("user");

            // 로그아웃된 사용자 정보를 출력
            log.info("Logged out user: {}", user.getName());
        }

        return "main";
    }

}
