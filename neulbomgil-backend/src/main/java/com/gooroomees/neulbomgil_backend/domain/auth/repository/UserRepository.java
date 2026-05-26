package com.gooroomees.neulbomgil_backend.domain.auth.repository;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.Role;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.Status;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);


    List<User> findByStatus(Status status);

    List<User> findByRole(Role role);
}