package com.tech.spotify.service;

import com.tech.spotify.Repository.*;
import com.tech.spotify.domain.*;
import com.tech.spotify.dto.PlaylistItemRequest;
import com.tech.spotify.dto.PlaylistRequest;
import com.tech.spotify.exception.PlaylistNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMusicService playlistMusicService;
    private final SpotifyService spotifyService;

    // 플레이리스트 등록
    public Playlist savePlaylist(PlaylistRequest playlistRequest, User loginUser) {
        String title = playlistRequest.gettitle();
        String desc = playlistRequest.getdesc();
        List<String> hashtags = playlistRequest.getHashtags();
        List<PlaylistItemRequest> playlistItems = playlistRequest.getPlaylistItems();

        Playlist savedPlaylist = Playlist.builder()
                .title(title)
                .description(desc)
                .hashtags(hashtags)
                .user(loginUser)
                .thumbnail(playlistItems.get(0).getAlbum_cover())
                .build();

        for (PlaylistItemRequest item : playlistItems) {

            String spotifyUri = spotifyService.getSpotifyUri(item.getTitle(), item.getArtist());
            String spotifyTrackId = spotifyUri.substring(spotifyUri.lastIndexOf(":") + 1);

            System.out.println(" playlistService spotifyUri = " + spotifyUri);
            System.out.println(" playlistService spotifyTrackId = " + spotifyTrackId);

            Music music = Music.builder()
                    .title(item.getTitle())
                    .artist(item.getArtist())
                    .album(item.getAlbum())
                    .album_cover_url(item.getAlbum_cover())
                    .time(item.getTime())
                    .playlist(savedPlaylist)
                    .spotify_uri(spotifyTrackId)
                    .build();

            PlaylistMusic playlistMusic = new PlaylistMusic();
            playlistMusic.setMusic(music);
            playlistMusic.setPlaylist(savedPlaylist);

            playlistMusicService.savePlaylistMusic(playlistMusic);
        }

        return savedPlaylist;
    }

    public Playlist findById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found with id: " + id));
    }

    @Transactional
    public void deletePlaylistById(Long playlistId) {
        playlistRepository.deleteById(playlistId);
    }

    public Playlist getPlaylistById(Long p_Id) {
        return playlistRepository.findById(p_Id)
                .orElseThrow(() -> new PlaylistNotFoundException("플레이리스트를 찾을 수 없습니다."));
    }

}



