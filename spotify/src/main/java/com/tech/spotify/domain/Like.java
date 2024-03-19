package com.tech.spotify.domain;

import com.tech.global.dto.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Userlike")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_good_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_good_playlist", nullable = false)
    private Playlist playlist;

}
