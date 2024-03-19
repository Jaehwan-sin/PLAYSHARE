package com.tech.spotify.controller;

import com.tech.spotify.domain.User;
import com.tech.spotify.dto.UserRequest;
import com.tech.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/user/login")
    public String login() {
        return "Login";
    }

    @PostMapping("/user/login")
    public String loginCheck(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        try {
            userService.validateLogin(email, password, session);
            return "redirect:/main";
        } catch (Exception e) {
            model.addAttribute("error", "로그인 실패하였습니다.");
            return "redirect:/user/login";
        }

    }


    @GetMapping("/user/new")
    public String sign_up() {
        return "Sign_up";
    }

    @PostMapping("/user/new")
    public String submitEmail(@RequestBody UserRequest request, RedirectAttributes attributes) {

        // 이메일 값을 attributes에 추가
        attributes.addAttribute("email", request.getemail());
        System.out.println("request.getemail = " + request.getemail());

        // /user/register로 리다이렉트
        return "redirect:/user/register";
    }

    @GetMapping("/user/register")
    public String register() {
        return "Register";
    }

    @PostMapping("/user/register")
    public String Register_info(@RequestBody UserRequest request) {

        User user = new User();
        user.setEmail(request.getemail());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        user.setName(request.getName());
        user.setHashTag1(request.getHashtag1());
        user.setHashTag2(request.getHashtag2());
        user.setHashTag3(request.getHashtag3());

        userService.join(user);

        return "user/main";
    }
}
