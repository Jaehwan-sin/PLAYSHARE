package com.tech.spotify.controller;

import com.tech.spotify.Repository.MusicRepository;
import com.tech.spotify.Repository.PlaylistRepository;
import com.tech.spotify.domain.Music;
import com.tech.spotify.domain.Playlist;
import com.tech.spotify.domain.User;
import com.tech.spotify.dto.PlaylistRequest;
import com.tech.spotify.service.CommentService;
import com.tech.spotify.service.LikeService;
import com.tech.spotify.service.PlaylistService;
import com.tech.spotify.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Controller
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;
    private final MusicRepository musicRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final SpotifyService spotifyService;

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
                           @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        handleUserLoginStatus(model, session);

        Page<Playlist> playlistPage = playlistRepository.findAll(pageable);
        model.addAttribute("playlistPage", playlistPage);

        List<Playlist> playlistList = playlistPage.getContent();

        int currentPage = playlistPage.getNumber();
        int totalPages = playlistPage.getTotalPages();

        model.addAttribute("playlistList", playlistList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);

        return "playlist";
    }


    @PostMapping("/user/playlist")
    public String playlist_add() {
        return "/playlist";
    }

    @GetMapping("/user/playlist_detail/{p_id}")
    public String playlistDetailView(@PathVariable Long p_id, Model model, HttpSession session) {
        handleUserLoginStatus(model, session);

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

        return "playlist_detail";
    }

    @GetMapping("/user/playlist_register")
    public String playlist_register(Model model, HttpSession session) {

        handleUserLoginStatus(model, session);

        return "playlist_Register";
    }

    @PostMapping("/user/playlist_register")
    public String playlist_register_data(@RequestBody PlaylistRequest playlistRequest, HttpSession session) {
        // 현재 로그인한 사용자의 정보를 가져옴
        User loginUser = (User) session.getAttribute("user");

        // 플레이리스트 등록
        Playlist savedPlaylist = playlistService.savePlaylist(playlistRequest, loginUser);

        return "redirect:/user/playlist";
    }

}
