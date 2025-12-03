package com.daniel_niepmann.registrations.domain.user.service;

import com.daniel_niepmann.registrations.common.exception.EntityAlreadyExistsException;
import com.daniel_niepmann.registrations.common.exception.EntityNotFoundException;
import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final EntityManager entityManager;

    private final Lock lock = new ReentrantLock();

    public void saveAll(Set<User> users) {
        users.forEach(user -> throwIfExistsById(user.getId()));
        userRepository.saveAll(users);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void failAllUsersInProgress() {
        List<User> allByStatus = findAllByStatus(Status.IN_PROGRESS);
        for (User byStatus : allByStatus) {
            byStatus.setStatus(Status.FAILED);
            byStatus.setErrorMessage("Registration timeout.");
        }
        userRepository.saveAll(allByStatus);
    }

    public User findRandomAvailableUser(Status status) {
        lock.lock();

        try {
            return (User) entityManager.createNativeQuery("""
                UPDATE users
                SET taken = true
                WHERE id = (
                    SELECT id
                    FROM users
                    WHERE taken = false OR taken is NULL
                      AND status = :status
                    ORDER BY RANDOM()
                    LIMIT 1
                    FOR UPDATE SKIP LOCKED
                )
                RETURNING *
                """, User.class)
                    .setParameter("status", status.name())
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("No users found.");
        } finally {
            lock.unlock();
        }
    }

    public List<User> findAllByStatus(Status status) {
        return userRepository.findAllByStatus(status);
    }

    public List<User> findAllByStatusesIn(Status... statuses) {
        return userRepository.findAllByStatusIn(List.of(statuses));
    }

    public List<User> findAllByIdIn(List<Long> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    public void update(Long id, User user) {
        User userById = findByIdOrThrow(id);

        Optional.ofNullable(user.getStatus()).ifPresent(status -> {
            if (status == Status.COMPLETED) {
                userById.setRegisteredAt(LocalDate.now());
                userById.setTaken(false);
            } else if (status == Status.FAILED) {
                userById.setTaken(false);
            }

            userById.setStatus(status);
        });
        Optional.ofNullable(user.getErrorMessage()).ifPresent(userById::setErrorMessage);
        Optional.ofNullable(user.getRegisteredAt()).ifPresent(userById::setRegisteredAt);

        userRepository.save(userById);
    }

    public void throwIfExistsById(Long id) {
        Optional<User> userOptional = findById(id);

        if (userOptional.isPresent()) {
            throw new EntityAlreadyExistsException("User already exists by email: " + userOptional.get().getEmail());
        }
    }

    public User findByIdOrThrow(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found by id: %d.".formatted(id));
        }

        return userOptional.get();
    }
}
