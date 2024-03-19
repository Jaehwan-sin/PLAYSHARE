package com.tech.spotify.controller;

import com.tech.spotify.domain.User;
import com.tech.spotify.dto.CommentRequest;
import com.tech.spotify.service.CommentService;
import com.tech.spotify.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping("/user/comments")
    public String comment(@RequestBody CommentRequest commentRequest
                                                    , HttpSession session) {

        String playlistId = commentRequest.getPlaylistId();
        log.info("playlistId : " + playlistId);

        User loginUser = (User) session.getAttribute("user");
        Long loginUserId = loginUser.getId();

        commentService.saveComment(commentRequest,playlistId,loginUser);

        return "playlist_detail";

    }

    // 댓글 수정
    @PutMapping("/user/comment_edit/{commentId}")
    public String comment_edit(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest
            , HttpSession session) {

        // 댓글 수정하는 사람 로그인 정보 가져오기
        User loginUser = (User) session.getAttribute("user");
        Long loginUserId = loginUser.getId();

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
        User loginUser = (User) session.getAttribute("user");
        Long loginUserId = loginUser.getId();

        // 삭제할 내용
        String delete_comment = commentRequest.getComment();

        // 삭제할 내용 처리
        commentService.deleteComment(commentId);

        log.info("PathVariable commentId : "+ commentId);

        return "playlist_detail";
    }

    @GetMapping("/user/like/{playlistId}")
    public ResponseEntity<?> checkLike(@PathVariable String playlistId, HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
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

        User loginUser = (User) session.getAttribute("user");
        Long loginUserId = loginUser.getId();

        likeService.like(playlistId, loginUser);

        return "playlist_detail";
    }

    @DeleteMapping("/user/unlike")
    public String unlike(@RequestBody CommentRequest commentRequest
            , HttpSession session) {

        String playlistId = commentRequest.getPlaylistId();
        log.info("playlistId : " + playlistId);

        User loginUser = (User) session.getAttribute("user");
        Long loginUserId = loginUser.getId();

        likeService.unlike(playlistId, loginUser);

        return "playlist_detail";
    }


}
