package com.tech.spotify.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistItemRequest {
    //mapstruct
    private String title;
    private String album;
    private String artist;
    private String addDate;
    private String time;
    private String album_cover;

}
