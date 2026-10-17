package org.example.repository;

import java.util.Optional;

public interface UrlRepository {

    Optional<String> findShortCodeByLongUrl(String longUrl);

    Optional<String> findLongUrlByShortCode(String shortCode);

    Optional<String> saveIfAbsent(String longUrl, String proposedShortCode);
}