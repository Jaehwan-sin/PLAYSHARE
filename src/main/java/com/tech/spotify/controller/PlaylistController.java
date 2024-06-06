package com.tech.spotify.controller;

import com.tech.spotify.Repository.MusicRepository;
import com.tech.spotify.Repository.PlaylistRepository;
import com.tech.spotify.Repository.UserRepository;
import com.tech.spotify.domain.Music;
import com.tech.spotify.domain.Playlist;
import com.tech.spotify.domain.User;
import com.tech.spotify.dto.PlaylistRequest;
import com.tech.spotify.service.*;
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
import java.util.*;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;
    private final MusicRepository musicRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final SpotifyService spotifyService;
    private final UserService userService;

    private boolean isAuthenticated(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("PlaylistController authentication = " + authentication);

        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            System.out.println("PlaylistController principal = " + principal);

            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                System.out.println("PlaylistController oAuth2User = " + oAuth2User);
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("userId", oAuth2User.getAttribute("name"));
                System.out.println("PlaylistController oAuth2User ID = " + oAuth2User);
                return true;
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("userId", userDetails.getUsername());
                System.out.println("PlaylistController UserDetails ID = " + userDetails.getUsername());
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

    @GetMapping("/user/playlist")
    public String playlist(Model model, HttpSession session,
                           @RequestParam(required = false) String search,
                           @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        // 인증되지 않은 경우 로그인 페이지로 리디렉션
        if (!isAuthenticated(model)) {
            return "redirect:/user/login";
        }

        long startTime = System.currentTimeMillis();
        log.info("startTime = "+startTime);

        Page<Playlist> playlistPage;

        // 검색어가 있는 경우 검색 결과를 가져옴
        if (search != null && !search.isEmpty()) {
            playlistPage = playlistService.searchPlaylists(search, pageable);
        } else {
            playlistPage = playlistRepository.findAll(pageable);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double durationInSeconds = duration / 1000.0;

        log.info("endTime = "+endTime);
        log.info("duration = "+duration);
        log.info("durationInSeconds = "+durationInSeconds);

        if (playlistPage == null) {
            playlistPage = Page.empty(pageable);
        }

        model.addAttribute("playlistPage", playlistPage);

        List<Playlist> playlistList = playlistPage.getContent();

        int currentPage = playlistPage.getNumber();
        int totalPages = playlistPage.getTotalPages();

        model.addAttribute("playlistList", playlistList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", search);

        return "playlist";
    }


    @PostMapping("/user/playlist")
    public String playlist_add() {
        return "/playlist";
    }

    @GetMapping("/user/playlist_detail/{p_id}")
    public String playlistDetailView(@PathVariable Long p_id, Model model, HttpSession session) {

        if (!isAuthenticated(model)) {
            return "redirect:/user/login";
        }

        long startTime = System.currentTimeMillis();
        log.info("Start time: " + startTime);

        // p_id로 playlist 테이블 조회
        Playlist playlist = playlistService.getPlaylistById(p_id);
        String playlist_title = playlist.getTitle();
        String description = playlist.getDescription();
        model.addAttribute("playlist_title", playlist_title);
        model.addAttribute("description", description);

        List<Music> musicList = musicRepository.findByPlaylistId(p_id);

        // Music 목록 및 기타 정보 로드
        model.addAttribute("musicList", musicList);
        model.addAttribute("comments", commentService.getCommentsByPlaylistId(p_id));
        model.addAttribute("commentCount", commentService.getCommentCountByPlaylistId(p_id));
        model.addAttribute("likeCount", likeService.getLikeCountByPlaylistId(p_id));

        // 이전글 다음글 가져오기
        Playlist previousPlaylist = playlistService.getPreviousPlaylist(p_id);
        Playlist nextPlaylist = playlistService.getNextPlaylist(p_id);
        model.addAttribute("previousPlaylist", previousPlaylist);
        model.addAttribute("nextPlaylist", nextPlaylist);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double durationInSeconds = duration / 1000.0;

        log.info("End time: " + endTime);
        log.info("Duration (milliseconds): " + duration);
        log.info("Duration (seconds): " + durationInSeconds);

        return "playlist_detail";
    }

    @GetMapping("/user/playlist_register")
    public String playlist_register(Model model, HttpSession session) {

        if (!isAuthenticated(model)) {
            return "redirect:/user/login";
        }

        return "playlist_Register";
    }

    @PostMapping("/user/playlist_register")
    public String playlist_register_data(@RequestBody PlaylistRequest playlistRequest, Model model, HttpSession session) {
        // 현재 로그인한 사용자의 정보를 가져옴
//         User loginUser = (User) session.getAttribute("user");

        isAuthenticated(model);

        // 현재 로그인한 사용자의 정보를 가져옴
        User loginUser = getCurrentUser();

        if (loginUser == null) {
            return "redirect:/user/login";
        }

        // 플레이리스트 등록
        Playlist savedPlaylist = playlistService.savePlaylist(playlistRequest, loginUser);

        return "redirect:/user/playlist";
    }

}
