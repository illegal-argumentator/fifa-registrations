package com.daniel_niepmann.registrations.domain.user.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String countryCode;

    private String state;

    private String city;

    private String street;

    private String houseNumber;

    private String postcode;

}
