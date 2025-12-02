package com.daniel_niepmann.registrations.service.user;

import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.daniel_niepmann.registrations.domain.user.common.builder.UserDataBuilder.buildUserFromLine;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUploadService {

    private final UserService userService;

    private final static String COMMA_SPLITERATOR = ",";

    public void uploadUsersFromLines(List<String> lines) {
        Set<User> users = new HashSet<>();

        for (String line : lines) {
            String[] cells = line.split(COMMA_SPLITERATOR);

            User user = buildUserFromLine(cells).build();
            users.add(user);
        }

        userService.saveAll(users);
    }

}
