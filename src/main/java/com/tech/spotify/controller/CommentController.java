package com.tech.spotify.controller;

import com.tech.spotify.domain.User;
import com.tech.spotify.dto.CommentRequest;
import com.tech.spotify.service.CommentService;
import com.tech.spotify.service.LikeService;
import com.tech.spotify.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;
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

    @PostMapping("/user/comments")
    public String comment(@RequestBody CommentRequest commentRequest
                                                    , HttpSession session) {

        String playlistId = commentRequest.getPlaylistId();
        log.info("playlistId : " + playlistId);

//        User loginUser = (User) session.getAttribute("user");
//        Long loginUserId = loginUser.getId();

        // 로그인 유저 조회 및 정보 가져오기
        User loginUser = getCurrentUser();

        commentService.saveComment(commentRequest,playlistId,loginUser);

        return "playlist_detail";

    }

    // 댓글 수정
    @PutMapping("/user/comment_edit/{commentId}")
    public String comment_edit(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest
            , HttpSession session) {

        // 댓글 수정하는 사람 로그인 정보 가져오기
//        User loginUser = (User) session.getAttribute("user");
//        Long loginUserId = loginUser.getId();

        // 로그인 유저 조회 및 정보 가져오기
        User loginUser = getCurrentUser();

        // 수정할 내용
        String edit_comment = commentRequest.getComment();

        // 수정할 내용 처리
        commentService.editComment(commentRequest, commentId, loginUser);

        log.info("PathVariable commentId : "+ commentId);
        log.info("edit_comment : "+ edit_comment);

        return "playlist_detail";
    }

    // 댓글 삭제
    @DeleteMapping("/user/comments_delete/{commentId}")
    public String comment_delete(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest
            , HttpSession session) {

        // 댓글 수정하는 사람 로그인 정보 가져오기
//        User loginUser = (User) session.getAttribute("user");
//        Long loginUserId = loginUser.getId();

        // 로그인 유저 조회 및 정보 가져오기
        User loginUser = getCurrentUser();

        // 삭제할 내용
        String delete_comment = commentRequest.getComment();

        // 삭제할 내용 처리
        commentService.deleteComment(commentId);

        log.info("PathVariable commentId : "+ commentId);

        return "playlist_detail";
    }

    @GetMapping("/user/like/{playlistId}")
    public ResponseEntity<?> checkLike(@PathVariable String playlistId, HttpSession session) {
//        User loginUser = (User) session.getAttribute("user");

        // 로그인 유저 조회 및 정보 가져오기
        User loginUser = getCurrentUser();

        if (loginUser == null) {
            // 로그인되지 않은 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 좋아요 여부 확인
        boolean isLiked = likeService.isLiked(playlistId, loginUser);

        if (isLiked) {
            // 이미 좋아요를 눌렀을 경우, 좋아요 정보를 반환
            return ResponseEntity.ok("Liked");
        } else {
            // 좋아요를 누르지 않았을 경우, 특정 값 또는 null 반환
            return ResponseEntity.ok("NotLiked");
        }
    }

    @PostMapping("/user/like")
    public String like(@RequestBody CommentRequest commentRequest
            , HttpSession session) {

        String playlistId = commentRequest.getPlaylistId();
        log.info("playlistId : " + playlistId);

        // User loginUser = (User) session.getAttribute("user");
        // Long loginUserId = loginUser.getId();

        // 로그인 유저 조회 및 정보 가져오기
        User loginUser = getCurrentUser();

        likeService.like(playlistId, loginUser);

        return "playlist_detail";
    }

    @DeleteMapping("/user/unlike")
    public String unlike(@RequestBody CommentRequest commentRequest
            , HttpSession session) {

        String playlistId = commentRequest.getPlaylistId();
        log.info("playlistId : " + playlistId);

//        User loginUser = (User) session.getAttribute("user");
//        Long loginUserId = loginUser.getId();

        // 로그인 유저 조회 및 정보 가져오기
        User loginUser = getCurrentUser();

        likeService.unlike(playlistId, loginUser);

        return "playlist_detail";
    }


}
