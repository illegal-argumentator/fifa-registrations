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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public User findRandomNotInUseUserAndPutInProgress() {
        lock.lock();

        try {
            User user = userRepository.findRandomLockedUser();
            update(user.getId(), User.builder().status(Status.IN_PROGRESS).build());
            return user;
        } catch (NoResultException e) {
            throw new EntityNotFoundException("No users found.");
        } finally {
            lock.unlock();
        }
    }

    public List<User> findAllByStatus(Status status) {
        return userRepository.findAllByStatusEquals(status);
    }

    public List<User> findAllByIdIn(List<Long> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    @Transactional
    public void update(Long id, User user) {
        User userById = findByIdOrThrow(id);

        Optional.ofNullable(user.getStatus()).ifPresent(status -> {
            if (status == Status.COMPLETED) {
                userById.setRegisteredAt(LocalDate.now());
            }

            userById.setStatus(status);
        });
        Optional.ofNullable(user.getErrorMessage()).ifPresent(userById::setErrorMessage);
        Optional.ofNullable(user.getRegisteredAt()).ifPresent(userById::setRegisteredAt);

        userRepository.save(userById);
        entityManager.flush();
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
