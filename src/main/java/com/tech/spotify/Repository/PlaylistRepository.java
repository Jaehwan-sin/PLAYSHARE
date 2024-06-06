package com.tech.spotify.Repository;

import com.tech.spotify.domain.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    Page<Playlist> findByTitleContaining(String title, Pageable pageable);

    Page<Playlist> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<Playlist> findById(Long id);

    void deleteById(Long playlistId);

    Page<Playlist> findByIdIn(List<Long> playlistIds, Pageable pageable);

    // 검색
    Page<Playlist> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);

    // 이전글
    Playlist findTopByIdLessThanOrderByIdDesc(Long pId);

    // 다음글
    Playlist findTopByIdGreaterThanOrderByIdAsc(Long id);

}
