package com.terangalink.backend.repository;

import com.terangalink.backend.entity.EmailVerificationToken;
import com.terangalink.backend.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EmailVerificationToken> findByToken(String token);

    boolean existsByToken(String token);

    List<EmailVerificationToken> findAllByUserAndUsedFalse(User user);

    Optional<EmailVerificationToken> findFirstByPendingEmailAndUsedFalseOrderByExpiresAtDesc(String pendingEmail);
}
