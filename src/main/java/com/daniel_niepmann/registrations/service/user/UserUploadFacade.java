package com.daniel_niepmann.registrations.service.user;

import com.daniel_niepmann.registrations.domain.user.service.CsvParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
