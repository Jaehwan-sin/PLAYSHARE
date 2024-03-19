package com.tech.spotify.service;

import com.tech.spotify.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    UserServiceTest(UserService userService,PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Test
        public void Join() throws Exception {
            // given
            User user = new User();
            String EncoderPassword = "1234";

            // when
            user.setName("신재환");
//            user.setPassword(passwordEncoder.encode(EncoderPassword));
            user.setEmail("fate427@naver.com");
            userService.join(user);

            // then
            Assertions.assertEquals("신재환", user.getName());
//            Assertions.assertTrue(passwordEncoder.matches(EncoderPassword, user.getPassword()));
            Assertions.assertEquals("fate427@naver.com", user.getEmail());

        }

}