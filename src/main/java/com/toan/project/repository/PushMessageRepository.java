package com.toan.project.repository;

import com.toan.project.models.Playlist;

import com.toan.project.models.PushMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PushMessageRepository extends JpaRepository<PushMessage, Long> {

}