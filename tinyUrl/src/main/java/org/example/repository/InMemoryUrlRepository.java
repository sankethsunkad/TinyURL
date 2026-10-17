package org.example.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUrlRepository implements UrlRepository {

    private final Map<String, String> longUrlToCode = new HashMap<>();
    private final Map<String, String> codeToLongUrl = new HashMap<>();

    @Override
    public synchronized Optional<String> findShortCodeByLongUrl(String longUrl) {
        return Optional.ofNullable(longUrlToCode.get(longUrl));
    }

    @Override
    public synchronized Optional<String> findLongUrlByShortCode(String shortCode) {
        return Optional.ofNullable(codeToLongUrl.get(shortCode));
    }

    @Override
    public synchronized Optional<String> saveIfAbsent(
            String longUrl,
            String proposedShortCode
    ) {
        String existingCode = longUrlToCode.get(longUrl);

        if (existingCode != null) {
            return Optional.of(existingCode);
        }

        if (codeToLongUrl.containsKey(proposedShortCode)) {
            return Optional.empty();
        }

        longUrlToCode.put(longUrl, proposedShortCode);
        codeToLongUrl.put(proposedShortCode, longUrl);

        return Optional.of(proposedShortCode);
    }
}