package com.toan.project.repository;

import java.util.Optional;

import com.toan.project.models.ERole;
import com.toan.project.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}