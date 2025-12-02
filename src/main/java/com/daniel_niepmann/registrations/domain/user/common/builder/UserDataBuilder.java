package com.daniel_niepmann.registrations.domain.user.common.builder;

import com.daniel_niepmann.registrations.domain.user.common.dto.Address;
import com.daniel_niepmann.registrations.domain.user.common.dto.Bio;
import com.daniel_niepmann.registrations.domain.user.common.type.Status;
import com.daniel_niepmann.registrations.domain.user.model.User;

import java.time.LocalDate;

public class UserDataBuilder {

    // TODO move to Map
    public static User.UserBuilder buildUserFromLine(String[] cells) {
        Address address = Address.builder()
                .city(cells[1])
                .countryCode(cells[2])
                .houseNumber(cells[8])
                .postcode(cells[15])
                .state(cells[18])
                .street(cells[19])
                .build();

        Bio bio = Bio.builder()
                .firstName(cells[7])
                .lastName(cells[11])
                .dateOfBirth(LocalDate.parse(cells[3]))
                .pii(cells[14])
                .sex(cells[17])
                .build();

        return User.builder()
                .id(Long.valueOf(cells[0]))
                .email(cells[5])
                .domain(cells[4])
                .password(cells[12])
                .status(Status.NOT_IN_USE)
                .phoneNumber(cells[13])
                .address(address)
                .bio(bio);
    }
}
