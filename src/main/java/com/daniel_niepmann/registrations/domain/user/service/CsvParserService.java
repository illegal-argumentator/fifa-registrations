package com.daniel_niepmann.registrations.domain.user.service;

import com.daniel_niepmann.registrations.common.exception.FileReadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CsvParserService {

    public List<String> retrieveAllLinesFromCsv(MultipartFile file, boolean skipFirstLine) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            if (skipFirstLine) {
                reader.readLine();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new FileReadException(e.getMessage());
        }

        return lines;
    }

}
