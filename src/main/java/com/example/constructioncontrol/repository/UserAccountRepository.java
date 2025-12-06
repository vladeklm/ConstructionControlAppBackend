package com.example.constructioncontrol.repository;

import com.example.constructioncontrol.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}
