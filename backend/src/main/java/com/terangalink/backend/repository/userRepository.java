package com.terangalink.backend.repository;

import com.terangalink.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepository  extends JpaRepository<User, Long> {
}
