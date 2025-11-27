package com.daniel_niepmann.registrations.domain.user.service;

import com.daniel_niepmann.registrations.common.exception.EntityAlreadyExistsException;
import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void throwIfExistsById(Long id) {
        Optional<User> userOptional = findById(id);

        if (userOptional.isPresent()) {
            throw new EntityAlreadyExistsException("User already exists by email: " + userOptional.get().getEmail());
        }
    }
}
