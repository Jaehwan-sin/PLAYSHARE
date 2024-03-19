package com.tech.spotify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String comments;
    private String commenter;
    private LocalDateTime registrationDate;  // 추가

    // 필요한 다른 필드들도 추가할 수 있습니다.

    public CommentResponse(Long id,String comments, String commenter, LocalDateTime registrationDate) {
        this.id = id;
        this.comments = comments;
        this.commenter = commenter;
        this.registrationDate = registrationDate;
    }


}
