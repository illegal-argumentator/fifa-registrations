package com.daniel_niepmann.registrations.domain.user.service;

import com.daniel_niepmann.registrations.common.exception.EntityAlreadyExistsException;
import com.daniel_niepmann.registrations.common.exception.EntityNotFoundException;
import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveAll(Set<User> users) {
        users.forEach(user -> throwIfExistsById(user.getId()));
        userRepository.saveAll(users);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllByStatus(Status status) {
        return userRepository.findAllByStatus(status);
    }

    public List<User> findAllByIdIn(List<Long> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    public void update(Long id, User user) {
        User userById = findByIdOrThrow(id);

        Optional.ofNullable(user.getStatus()).ifPresent(userById::setStatus);
        Optional.ofNullable(user.getErrorMessage()).ifPresent(userById::setErrorMessage);

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

        if (userOptional.isPresent()) {
            throw new EntityNotFoundException("User not found by id: %d.".formatted(id));
        }

        return userOptional.get();
    }
}
