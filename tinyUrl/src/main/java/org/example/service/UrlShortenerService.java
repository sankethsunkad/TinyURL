package org.example.service;

import org.example.exception.UrlNotFoundException;
import org.example.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
public class UrlShortenerService {

    private final UrlRepository urlRepository;
    private final String baseUrl;

    public UrlShortenerService(
            UrlRepository urlRepository,
            @Value("${app.base-url}") String baseUrl
    ) {
        this.urlRepository = urlRepository;
        this.baseUrl = baseUrl;
    }

    public String shorten(String inputUrl) {
        String longUrl = normalizeAndValidate(inputUrl);

        return urlRepository.findShortCodeByLongUrl(longUrl)
                .map(this::buildShortUrl)
                .orElseGet(() -> createNewShortUrl(longUrl));
    }

    public String resolve(String shortCode) {
        return urlRepository.findLongUrlByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException(
                                "Short URL not found: " + shortCode
                        )
                );
    }

    private String createNewShortUrl(String longUrl) {
        while (true) {
            String proposedCode = generateShortCode();

            var savedCode =
                    urlRepository.saveIfAbsent(longUrl, proposedCode);

            if (savedCode.isPresent()) {
                return buildShortUrl(savedCode.get());
            }
        }
    }

    private String generateShortCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 7);
    }

    private String buildShortUrl(String shortCode) {
        return baseUrl + "/" + shortCode;
    }

    private String normalizeAndValidate(String inputUrl) {
        if (inputUrl == null || inputUrl.isBlank()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        String normalized = inputUrl.trim();

        if (!normalized.startsWith("http://")
                && !normalized.startsWith("https://")) {
            normalized = "https://" + normalized;
        }

        try {
            URI uri = new URI(normalized);

            if (uri.getHost() == null) {
                throw new IllegalArgumentException("Invalid URL");
            }

            return normalized;
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("Invalid URL");
        }
    }
}