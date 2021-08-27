package com.toan.project.repository;

import com.toan.project.models.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Page<Playlist> findByNameContaining(String title, Pageable pageable);
    List<Playlist> findAllByOrderByCreationDateAsc();
}
