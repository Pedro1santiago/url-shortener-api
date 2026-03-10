package urlshortener.adapters.in.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.dto.CreateShortUrlResponse;
import urlshortener.application.service.QrCodeService;
import urlshortener.application.service.ShortUrlService;

import java.net.URI;

@RestController
@RequestMapping
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final QrCodeService qrCodeService;
    private final String baseUrl;

    public ShortUrlController(
            ShortUrlService shortUrlService,
            QrCodeService qrCodeService,
            @Value("${app.base-url}") String baseUrl
    ) {
        this.shortUrlService = shortUrlService;
        this.qrCodeService = qrCodeService;
        this.baseUrl = baseUrl;

        if (this.baseUrl == null || this.baseUrl.isBlank()) {
            throw new RuntimeException("app.base-url property is not set");
        }
    }

    /** Builds the API response payload for a newly created short URL. */
    private CreateShortUrlResponse buildResponse(String code, String originalUrl) {
        return new CreateShortUrlResponse(
                baseUrl + "/" + code,
                code,
                originalUrl
        );
    }

    /** Creates a short URL using a custom code provided by the client. */
    @PostMapping("/short-urls/custom")
    public ResponseEntity<CreateShortUrlResponse> createCustomShortUrl(@RequestBody CreateShortUrlRequest request) {

        String code = shortUrlService.createCustomShortCode(request);

        CreateShortUrlResponse response = buildResponse(code, request.originalUrl());

        return ResponseEntity
                .created(URI.create(baseUrl + "/" + code))
                .body(response);
    }

    /** Creates a short URL using a randomly generated code. */
    @PostMapping("/short-urls")
    public ResponseEntity<CreateShortUrlResponse> createRandomShortUrl(
            @RequestBody CreateShortUrlRequest request) {

        String code = shortUrlService.createRandomShortCode(request);

        CreateShortUrlResponse response = buildResponse(code, request.originalUrl());

        return ResponseEntity
                .created(URI.create(baseUrl + "/" + code))
                .body(response);
    }

    /** Redirects to the original URL for the given short code. */
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable("code") String shortCode) {

        String originalUrl = shortUrlService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    /** Returns a PNG QR code image for the short URL. */
    @GetMapping("/{code}/qr-code")
    public ResponseEntity<byte[]> qrCode(@PathVariable("code") String shortCode) throws Exception {

        String shortUrl = baseUrl + "/" + shortCode;

        byte[] qrCode = qrCodeService.generate(shortUrl);

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/png")
                .body(qrCode);
    }

    /** Returns a PNG QR code image as an attachment download. */
    @GetMapping("/{code}/qr-code/download")
    public ResponseEntity<byte[]> downloadQrCode(@PathVariable("code") String shortCode) throws Exception {

        String shortUrl = baseUrl + "/" + shortCode;

        byte[] qrCode = qrCodeService.generate(shortUrl);

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/png")
                .header("Content-Disposition", "attachment; filename=\"qrcode-" + shortCode + ".png\"")
                .body(qrCode);
    }

//    @GetMapping("/{code}/stats")
//    public ResponseEntity<Long> getClicks(@PathVariable String code) {
//
//        Long clicks = shortUrlService.getClicks(code);
//
//        return ResponseEntity.ok(clicks);
//    }
}