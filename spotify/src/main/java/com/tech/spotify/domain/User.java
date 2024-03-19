package com.tech.spotify.domain;

import com.tech.global.dto.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "u_name")
    private String name;

    @Column(name = "u_email")
    private String email;

    @Column(name = "u_password")
    private String password;

    @Column(name = "u_hash_tag_1")
    private String hashTag1;

    @Column(name = "u_hash_tag_2")
    private String hashTag2;

    @Column(name = "u_hash_tag_3")
    private String hashTag3;

    @OneToMany(mappedBy = "user")
    private List<Playlist> playlists = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Like> like = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comments> comments = new ArrayList<>();

    // 비밀번호 검증 로직
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

}

