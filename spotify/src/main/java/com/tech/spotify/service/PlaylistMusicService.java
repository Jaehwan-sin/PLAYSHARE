package com.tech.spotify.service;

import com.tech.spotify.Repository.*;
import com.tech.spotify.domain.PlaylistMusic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaylistMusicService {

    private final PlaylistMusicRepository playlistMusicRepository2;

    @Transactional
    public PlaylistMusic savePlaylistMusic(PlaylistMusic playlistMusic) {
        // 현재 Playlist에 속한 PlaylistMusic 중 최대 sequence 값을 찾습니다.
        Integer maxSequence = playlistMusicRepository2.findMaxSequenceByPlaylistId();

        // 최대 sequence가 null이면 0으로 초기화합니다.
        maxSequence = (maxSequence == null) ? 0 : maxSequence;

        // 다음 sequence 값을 설정합니다.
        playlistMusic.setSequence(maxSequence + 1);

        return playlistMusicRepository2.save(playlistMusic);
    }

}
