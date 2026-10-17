package org.example.controller;

import org.example.dto.ResolveUrlResponse;
import org.example.dto.ShortenUrlRequest;
import org.example.dto.ShortenUrlResponse;
import org.example.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/api/v1/urls")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
            @RequestBody ShortenUrlRequest request
    ) {
        String shortUrl =
                urlShortenerService.shorten(request.longUrl());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ShortenUrlResponse(shortUrl));
    }

    @GetMapping("/api/v1/urls/{shortCode}")
    public ResolveUrlResponse resolveUrl(
            @PathVariable String shortCode
    ) {
        String longUrl =
                urlShortenerService.resolve(shortCode);

        return new ResolveUrlResponse(longUrl);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode
    ) {
        String longUrl =
                urlShortenerService.resolve(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}