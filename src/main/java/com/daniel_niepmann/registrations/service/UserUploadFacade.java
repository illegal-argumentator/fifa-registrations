package com.daniel_niepmann.registrations.service;

import com.daniel_niepmann.registrations.domain.user.service.CsvParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Class which implements Facade pattern for parsing CSV file and parsing data to user entity and saving to db.
 */
@Service
@RequiredArgsConstructor
public class UserUploadFacade {

    private final CsvParserService csvParserService;

    private final UserUploadService userUploadService;

    public void uploadUsersFromCsv(MultipartFile file, boolean skipFirstLine) {
        List<String> lines = csvParserService.retrieveAllLinesFromCsv(file, skipFirstLine);
        userUploadService.uploadUsersFromLines(lines);
    }

}
