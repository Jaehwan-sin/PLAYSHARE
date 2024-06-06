package com.tech.spotify.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tech.jwt.JwtProperties;
import com.tech.spotify.Repository.UserRepository;
import com.tech.spotify.config.oauth.provider.OAuth2UserInfo;
import com.tech.spotify.config.oauth.provider.GoogleUserInfo;
import com.tech.spotify.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwtCreateController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/oauth/jwt/google")
    public ResponseEntity<String> jwtCreate(@RequestBody Map<String, Object> data) {
        System.out.println("jwtCreate 실행됨");
        System.out.println(data.get("profileObj"));
        OAuth2UserInfo GoogleUserInfo =
                new GoogleUserInfo((Map<String, Object>)data.get("profileObj"));

        User userEntity =
                userRepository.findByUsername(GoogleUserInfo.getProvider()+"_"+GoogleUserInfo.getProviderId());

        if(userEntity == null) {
            User userRequest = User.builder()
                    .username(GoogleUserInfo.getProvider()+"_"+GoogleUserInfo.getProviderId())
                    .password(bCryptPasswordEncoder.encode("겟인데어"))
                    .email(GoogleUserInfo.getEmail())
                    .provider(GoogleUserInfo.getProvider())
                    .providerID(GoogleUserInfo.getProviderId())
                    .roles("ROLE_USER")
                    .build();

            userEntity = userRepository.save(userRequest);
        }

        String jwtToken = JWT.create()
                .withSubject(userEntity.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
                .withClaim("id", userEntity.getId())
                .withClaim("username", userEntity.getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        return ResponseEntity.ok().body(jwtToken);
    }

}
