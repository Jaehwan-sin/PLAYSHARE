package com.tech.spotify.service;

import com.tech.spotify.Repository.CommentRepository;
import com.tech.spotify.domain.Comments;
import com.tech.spotify.domain.Playlist;
import com.tech.spotify.domain.User;
import com.tech.spotify.dto.CommentRequest;
import com.tech.spotify.dto.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PlaylistService playlistService;

    @Autowired
    public CommentService(CommentRepository commentRepository, PlaylistService playlistService) {
        this.commentRepository = commentRepository;
        this.playlistService = playlistService;
    }

    public void saveComment(CommentRequest commentRequest, String playlistId, User loginUser) {
        // PlaylistService를 사용하여 Playlist 엔터티 가져오기
        Playlist playlist = playlistService.findById(Long.valueOf(playlistId));

        // Comments 엔터티 빌더를 사용하여 생성
        Comments comment = Comments.builder()
                .comments(commentRequest.getComment())
                .user(loginUser)
                .playlist(playlist)
                .build();

        commentRepository.save(comment);
    }

    // 플레이리스트에 등록된 댓글 정보 가져오기
    public List<CommentResponse> getCommentsByPlaylistId(Long playlistId) {
        List<Comments> commentsList = commentRepository.findByPlaylistIdOrderByCreatedAtDesc(playlistId);
        System.out.println("commentsList = " + commentsList);
        return commentsList.stream()
                .map(comment -> new CommentResponse(comment.getId(),comment.getComments(), comment.getUser().getName(), comment.getRegistration_date()))
                .collect(Collectors.toList());
    }

    // 댓글 수 가져오기
    public int getCommentCountByPlaylistId(Long pId) {
        return (int) commentRepository.countByPlaylistId(pId);
    }

    public void editComment(CommentRequest commentRequest, Long commentId, User loginUser) {

        Optional<Comments> optionalComment = commentRepository.findById(commentId);

        // Comment가 존재하면 수정 진행
        optionalComment.ifPresent(comment -> {
            // 수정할 내용 설정
            comment.setComment(commentRequest.getComment());
            comment.setModifty_date(LocalDateTime.now());
            // 다른 필요한 수정 로직 추가

            // 수정된 Comment를 저장
            commentRepository.save(comment);
        });
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Optional<Comments> deletecomment = commentRepository.findById(commentId);

        // Comment가 존재하면 삭제 진행
        deletecomment.ifPresent(commentRepository::delete);
    }

}
