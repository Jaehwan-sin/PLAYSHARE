package com.tech.spotify.domain;

import com.tech.global.dto.BaseEntity;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlist")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Playlist extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    @Column(name = "p_thumbnail")
    private String thumbnail;

    @Column(name = "p_title")
    private String title;

    @Column(name = "p_description")
    private String description;

    @Column(name = "p_hash_tag")
    @ElementCollection
    private List<String> hashtags;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> like = new ArrayList<>();

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistMusic> playlistMusics = new ArrayList<>();

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>();

    @Builder
    public Playlist(Long id, User user, String thumbnail, String title, String description, LocalDateTime registrationDate, LocalDateTime modifyDate, List<String> hashtags, List<Like> like, List<PlaylistMusic> playlistMusics, List<Comments> comments) {
        super(id, registrationDate, modifyDate);

        this.user = user;
        this.thumbnail = thumbnail;
        this.title = title;
        this.description = description;
        this.hashtags = hashtags;
        this.like = like; 
        this.playlistMusics = playlistMusics;
        this.comments = comments;
    }

}


