package com.tech.spotify.Repository;

import com.tech.spotify.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findOneById(Long id);

}
