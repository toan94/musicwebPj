package com.toan.project.repository;

import com.toan.project.models.Song;
import com.toan.project.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository  extends JpaRepository<Song, Long> {
    Page<Song> findByNameContaining(String title, Pageable pageable);
    Page<Song> findByGenreContaining(String genre, Pageable pageable);
    Page<Song> findByGenreAndNameContaining(String genre, String title, Pageable pageable);

}