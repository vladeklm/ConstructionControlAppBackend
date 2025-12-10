package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByLogin(String login);
    Optional<UserAccount> findByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
}
