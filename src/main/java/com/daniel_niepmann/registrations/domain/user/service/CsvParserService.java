package com.daniel_niepmann.registrations.domain.user.service;

import ch.qos.logback.core.util.StringUtil;
import com.daniel_niepmann.registrations.common.exception.FileReadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which parses data from MultipartFile to array of lines.
 */
@Slf4j
@Service
public class CsvParserService {

    public List<String> retrieveAllLinesFromCsv(MultipartFile file, boolean skipFirstLine) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            if (skipFirstLine) {
                boolean skippedFirstLine = skipFirstLine(reader);
                if (skippedFirstLine) {
                    return List.of();
                }
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

    public boolean skipFirstLine(BufferedReader reader) throws IOException {
        return !StringUtil.isNullOrEmpty(reader.readLine());
    }

}
