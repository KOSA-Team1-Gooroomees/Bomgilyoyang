package com.gooroomees.neulbomgil_backend.domain.auth.service;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.CustomUserDetails;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.UserAuth;
import com.gooroomees.neulbomgil_backend.domain.auth.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAuthRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAuth user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.: " + email));

        return new CustomUserDetails(user);
    }
}
