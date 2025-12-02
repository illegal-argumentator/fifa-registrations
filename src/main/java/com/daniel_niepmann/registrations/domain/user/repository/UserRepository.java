package com.daniel_niepmann.registrations.domain.user.repository;

import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByStatus(Status status);

    List<User> findAllByIdIn(List<Long> ids);

}
