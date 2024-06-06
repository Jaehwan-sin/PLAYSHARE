package com.tech.spotify.controller;

import com.tech.jwt.JwtProperties;
import com.tech.spotify.domain.User;
import com.tech.spotify.dto.UserRequest;
import com.tech.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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

    @GetMapping("/user/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            model.addAttribute("errorMessage", "로그인 실패했습니다. 다시 시도해주세요.");
        }

        return "Login";
    }

//    @PostMapping("/user/login")
//    public String loginCheck(@RequestParam("email") String email,
//                             @RequestParam("password") String password,
//                             HttpSession session, Model model, RedirectAttributes redirectAttributes) {
//
//        return "redirect:/main";
//    }


    @PostMapping("/user/login")
    public String loginCheck(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("email = " + email);
        System.out.println("password = " + password);
        if (isAuthenticated(model)) {
            return "main";
        }

        return "main";
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

        user.setUsername(request.getName());
        user.setHashTag1(request.getHashtag1());
        user.setHashTag2(request.getHashtag2());
        user.setHashTag3(request.getHashtag3());
        user.setRoles("ROLE_USER");

        userService.join(user);

        return "/main";
    }
}
