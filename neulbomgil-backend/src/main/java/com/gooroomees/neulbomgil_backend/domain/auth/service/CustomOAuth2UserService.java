package com.gooroomees.neulbomgil_backend.domain.auth.service;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.CustomUserDetails;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.Role;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.Status;
import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import com.gooroomees.neulbomgil_backend.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info(attributes.toString());
        String providerId = attributes.get("id").toString();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        String email = kakaoAccount.get("email").toString();
        log.info(email);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // 최초 로그인 시 회원가입 로직
            user = User.builder()
                    .name("카카오" + (Math.random()*1000 + 1))
                    .email(email)
                    .password(null)
                    .role(Role.USER)
                    .status(Status.ACTIVE)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(user);
        }

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}
