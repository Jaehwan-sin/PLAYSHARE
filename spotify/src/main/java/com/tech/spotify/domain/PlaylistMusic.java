package com.tech.spotify.domain;

import com.tech.global.dto.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "playlist_music")
@Getter
@Setter
@NoArgsConstructor
public class PlaylistMusic extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "p_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "m_id", nullable = false)
    private Music music;

    @Column(name = "pm_sequence")
    private Integer sequence;

}
