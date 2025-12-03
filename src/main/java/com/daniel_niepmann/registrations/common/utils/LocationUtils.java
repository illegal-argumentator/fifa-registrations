package com.daniel_niepmann.registrations.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class LocationUtils {

    public static final String EN_BRITAIN_LANGUAGE_CODE = "en-GB";

    public static final String EN_AMERICAN_LANGUAGE_CODE = "en-US";

    public static final String EN_LANGUAGE_CODE = "en";

    public static String getCountryFromCountryCode(String countryCode) {
        Locale countryLocale = new Locale("", countryCode.toUpperCase());
        return countryLocale.getDisplayCountry(Locale.US);
    }

}
