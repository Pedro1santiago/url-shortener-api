package urlshortener.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.dto.CreateShortUrlResponse;
import urlshortener.service.QrCodeService;
import urlshortener.service.ShortUrlService;

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

    @PostMapping("/short-urls/custom")
    public ResponseEntity<CreateShortUrlResponse> createCustomShortUrl(
            @RequestBody CreateShortUrlRequest request) {

        String code = shortUrlService.createCustomShortCode(request);

        return ResponseEntity.ok(
                new CreateShortUrlResponse(
                        baseUrl + "/" + code,
                        code,
                        request.originalUrl()
                )
        );
    }

    @PostMapping("/short-urls")
    public ResponseEntity<CreateShortUrlResponse> createRandomShortUrl(
            @RequestBody CreateShortUrlRequest request) {

        String code = shortUrlService.createRandomShortCode(request);

        return ResponseEntity.ok(
                new CreateShortUrlResponse(
                        baseUrl + "/" + code,
                        code,
                        request.originalUrl()
                )
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable("code") String shortCode) {

        String originalUrl = shortUrlService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/{code}/qr-code")
    public ResponseEntity<byte[]> qrCode(@PathVariable("code") String shortCode) throws Exception {

        String shortUrl = baseUrl + "/" + shortCode;

        byte[] qrCode = qrCodeService.generate(shortUrl);

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/png")
                .body(qrCode);
    }

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
}