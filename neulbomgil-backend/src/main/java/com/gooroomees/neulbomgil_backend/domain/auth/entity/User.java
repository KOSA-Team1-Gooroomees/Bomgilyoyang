package com.gooroomees.neulbomgil_backend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String name;

    private Role role;

    private Status status;

    // OAuth2 연동을 위한 필드
    private String provider;
    private String providerId;

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public void changeStatus(Status status) {
        this.status = status;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void deleteUser() {
        this.status = Status.REMOVED;
    }
}