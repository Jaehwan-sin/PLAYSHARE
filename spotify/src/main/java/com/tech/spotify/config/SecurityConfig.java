package com.tech.spotify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //jwt 토큰

//        http
//                .authorizeRequests()
//                .antMatchers("/**", "/img/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .csrf().disable()
//                .cors(); // CORS 설정 추가

        http
                .authorizeRequests()
                    .antMatchers("/**", "/img/**").permitAll()
                    .antMatchers("/user/playlist_register", "/user/playlist_detail").authenticated()
                    .anyRequest().authenticated()
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .csrf().disable()
                .cors();
    }

    // 인증되지 않은 사용자에 대한 처리를 위한 AuthenticationEntryPoint 설정
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("로그인이 필요한 서비스입니다."); // 알림창에 표시할 메시지
        return entryPoint;
    }

}

