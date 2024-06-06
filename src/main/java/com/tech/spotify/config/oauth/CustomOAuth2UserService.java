package com.tech.spotify.config.oauth;

import com.tech.spotify.Repository.UserRepository;
import com.tech.spotify.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        return processOAuth2User(userRequest, user);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        System.out.println("CustomOAuth2UserService attributes = " + attributes);
        String providerID = userRequest.getClientRegistration().getRegistrationId();  // 공급자 ID, 예: "google", "facebook"
        System.out.println("CustomOAuth2UserService providerID = " + providerID);

        String email;
        if ("naver".equals(providerID)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            attributes = response;  // response 객체를 attributes로 설정
        } else {
            email = (String) attributes.get("email");
        }

        System.out.println("CustomOAuth2UserService email = " + email);
        User user = userRepository.findByEmailAndproviderID(email, providerID);
        System.out.println("CustomOAuth2UserService user = " + user);

        if (user == null) {
            user = registerNewUser(providerID, attributes);
        } else {
            user = updateExistingUser(user, attributes, providerID);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));  // 사용자 역할 설정

        String nameAttributeKey = "email";
        if (!attributes.containsKey("email")) {
            nameAttributeKey = "id";  // 'email' 속성이 없는 경우 'id'를 이름 속성으로 사용
        }

        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);  // nameAttributeKey 사용
    }

    private User registerNewUser(String providerId, Map<String, Object> attributes) {
        User user = new User();
        user.setProviderID(providerId);
        if ("naver".equals(providerId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            user.setEmail((String) response.get("email"));
            user.setUsername((String) response.get("name"));
        } else {
            user.setEmail((String) attributes.get("email"));
            user.setUsername((String) attributes.get("name"));
        }
        userRepository.save(user);
        return user;
    }

    private User updateExistingUser(User existingUser, Map<String, Object> attributes, String providerID) {
        if ("naver".equals(providerID)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response != null) {
                existingUser.setUsername((String) response.get("name"));  // 최신 이름으로 업데이트
            } else {
                existingUser.setUsername((String) attributes.get("name"));  // 최신 이름으로 업데이트
            }
        } else {
            existingUser.setUsername((String) attributes.get("name"));  // 최신 이름으로 업데이트
        }
        userRepository.save(existingUser);
        return existingUser;
    }
}
