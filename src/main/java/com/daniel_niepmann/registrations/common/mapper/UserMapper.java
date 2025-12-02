package com.daniel_niepmann.registrations.common.mapper;

import com.daniel_niepmann.registrations.domain.user.model.User;
import com.daniel_niepmann.registrations.web.dto.UpdateUserRequest;

public class UserMapper {

    public static User mapUpdateUserRequestToUser(UpdateUserRequest updateUserRequest) {
        return User.builder()
                .status(updateUserRequest.status())
                .errorMessage(updateUserRequest.errorMessage())
                .build();
    }

}
