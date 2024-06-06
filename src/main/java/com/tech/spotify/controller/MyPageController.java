package com.tech.spotify.controller;

import com.tech.spotify.Repository.PlaylistRepository;
import com.tech.spotify.domain.Playlist;
import com.tech.spotify.domain.User;
import com.tech.spotify.service.LikeService;
import com.tech.spotify.service.PlaylistService;
import com.tech.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final PlaylistRepository playlistRepository;
    private final PlaylistService playlistService;
    private final LikeService likeService;
    private final UserService userService;

    // 로그인 검증 메서드
    private void handleUserLoginStatus(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Long userId = user.getId();
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userId", userId);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
    }

    private boolean isAuthenticated(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("userId", oAuth2User.getAttribute("name"));
                return true;
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("userId", userDetails.getUsername());
                return true;
            } else {
                // Handle other types of principal
                System.out.println("Unknown principal type: " + principal.getClass());
            }
        }
        return false;
    }

    // 로그인 유저 정보 조회하기
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                return userService.findByUsername(userDetails.getUsername());
            } else if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                String username = oAuth2User.getAttribute("name");
                return userService.findByUsername(username);
            }
        }
        return null;
    }

    @GetMapping("user/mypage")
    public String mypage(Model model, HttpSession session) {

        if (!isAuthenticated(model)) {
            return "redirect:/user/login";
        } else {
            isAuthenticated(model);
        }

        User loginUser = getCurrentUser();

        return "My_Page/My_page";
    }

    @GetMapping("/My_Page/My_profile")
    public String loadMyProfile(Model model, HttpSession session) {

//        handleUserLoginStatus(model, session);

        // User user = (User) session.getAttribute("user");
        // Long LoginUserId = user.getId();

        if (!isAuthenticated(model)) {
            return "redirect:/user/login";
        } else {
            isAuthenticated(model);
        }

        User user = getCurrentUser();
        Long LoginUserId = user.getId();

        String userId = user.getEmail();
        String password =  user.getPassword();
        String name = user.getUsername();
        String hashtag1 = user.getHashTag1();
        String hashtag2 = user.getHashTag2();
        String hashtag3 = user.getHashTag3();

        StringBuilder hashtagsBuilder = new StringBuilder();

        if (hashtag1 != null) {
            hashtagsBuilder.append(hashtag1);
            hashtagsBuilder.append(", "); 
        }

        if (hashtag2 != null) {
            hashtagsBuilder.append(hashtag2);
            hashtagsBuilder.append(", "); 
        }

        if (hashtag3 != null) {
            hashtagsBuilder.append(hashtag3);
        }

        String hashtags = hashtagsBuilder.toString();

        // 마지막에 쉼표 제거
        if (hashtags.endsWith(", ")) {
            hashtags = hashtags.substring(0, hashtags.length() - 2);
        }

        model.addAttribute("userId", userId);
        model.addAttribute("password", password);
        model.addAttribute("name", name);
        model.addAttribute("hashtags", hashtags);

        return "My_Page/My_profile";
    }

    @GetMapping("/My_Page/My_playlist")
    public String loadMyPlaylist(Model model, HttpSession session,
                                 @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

//        handleUserLoginStatus(model, session);

//        User user = (User) session.getAttribute("user");
//        Long LoginUserId = user.getId();
//        String LoginUserName = user.getUsername();

        if (!isAuthenticated(model)) {
            return "redirect:/user/login";
        } else {
            isAuthenticated(model);
        }

        User user = getCurrentUser();
        Long LoginUserId = user.getId();
        String LoginUserName = user.getUsername();

        Page<Playlist> playlistPage = playlistRepository.findAllByUserId(LoginUserId, pageable);

        model.addAttribute("LoginUserName", LoginUserName);
        model.addAttribute("playlistPage", playlistPage);

        List<Playlist> playlistList = playlistPage.getContent();

        int currentPage = playlistPage.getNumber();
        int totalPages = playlistPage.getTotalPages();

        model.addAttribute("playlistList", playlistList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);

        return "My_Page/My_playlist";
    }

    @DeleteMapping("/My_playlist/delete")
    public String playlist_delete(@RequestParam String playlistId, Model model, HttpSession session) {

//        handleUserLoginStatus(model, session);
//
//        User user = (User) session.getAttribute("user");
//        Long LoginUserId = user.getId();

        if (!isAuthenticated(model)) {
            return "redirect:/user/login";
        } else {
            isAuthenticated(model);
        }

        User user = getCurrentUser();
        Long LoginUserId = user.getId();

        log.info("DeleteMapping playlistId : "+playlistId);

        // playlistId 로 삭제하는 로직
        playlistService.deletePlaylistById(Long.valueOf(playlistId));

        return "My_Page/My_playlist";
    }

    @GetMapping("/My_Page/My_like")
    public String loadMyLike(Model model, HttpSession session,
                             @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

//        handleUserLoginStatus(model, session);
//
//        User user = (User) session.getAttribute("user");
//        Long LoginUserId = user.getId();
//        String LoginUsername = user.getUsername();

        if (!isAuthenticated(model)) {
            return "redirect:/user/login";
        } else {
            isAuthenticated(model);
        }

        User user = getCurrentUser();
        Long LoginUserId = user.getId();
        String LoginUsername = user.getUsername();

        Page<Playlist> playlistPage = likeService.findPlaylistsByLikeId(LoginUserId, pageable);

        // playlists를 model에 추가
        model.addAttribute("playlistPage", playlistPage);
        model.addAttribute("LoginUsername", LoginUsername);

        List<Playlist> playlistList = playlistPage.getContent();

        int currentPage = playlistPage.getNumber();
        int totalPages = playlistPage.getTotalPages();

        model.addAttribute("playlistList", playlistList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);

        return "My_Page/My_like";
    }

    @DeleteMapping("/My_Like_playlist/delete")
    public String Like_playlist_delete(@RequestParam String playlistId, Model model, HttpSession session) {

        handleUserLoginStatus(model, session);

        User loginUser = getCurrentUser();

//        User loginUser = (User) session.getAttribute("user");

        log.info("DeleteMapping playlistId : "+playlistId);

        // 좋아요 플레이리스트에서 좋아요 취소 하는 로직
        likeService.unlike(playlistId, loginUser);

        return "My_Page/My_playlist";
    }

}
