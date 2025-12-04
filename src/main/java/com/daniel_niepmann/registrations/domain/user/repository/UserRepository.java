package com.daniel_niepmann.registrations.domain.user.repository;

import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByStatusEquals(Status status);

    List<User> findAllByIdIn(List<Long> ids);

    @Query(value = """
        SELECT *
        FROM users
        WHERE status = 'NOT_IN_USE'
        ORDER BY RANDOM()
        LIMIT 1
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    User findRandomLockedUser();

}
