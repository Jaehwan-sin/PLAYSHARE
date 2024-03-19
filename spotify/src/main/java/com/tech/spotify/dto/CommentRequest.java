package com.tech.spotify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {

    private Long commentId;  // 수정된 부분: commentId를 추가함
    private String comment;
    private String playlistId;

    public CommentRequest(Long commentId, String comment, String playlistId) {
        this.commentId = commentId;
        this.comment = comment;
        this.playlistId = playlistId;
    }

}
