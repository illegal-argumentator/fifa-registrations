package com.daniel_niepmann.registrations.domain.user.repository;

import com.daniel_niepmann.registrations.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
