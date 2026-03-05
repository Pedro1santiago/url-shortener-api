package urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.dto.RequestDTO;
import urlshortener.dto.ResponseDTO;
import urlshortener.service.QrCodeService;
import urlshortener.service.UrlService;

import java.net.URI;

@RestController
@RequestMapping()
public class UrlController {

    private final UrlService urlService;
    private final QrCodeService qrCodeService;
    private final String baseUrl;

    public UrlController(UrlService urlService, QrCodeService qrCodeService) {
        this.urlService = urlService;
        this.qrCodeService = qrCodeService;

        this.baseUrl = System.getenv("BASE_URL");

        if (this.baseUrl == null || this.baseUrl.isBlank()) {
            throw new RuntimeException("BASE_URL environment variable not set");
        }
    }

    @PostMapping("/shortener")
    public ResponseEntity<ResponseDTO> createUrl(@RequestBody RequestDTO dto) {
        return ResponseEntity.ok(urlService.createUrl(dto));
    }

    @GetMapping("/{urlName}")
    public ResponseEntity<Void> callUrl(@PathVariable String urlName) {

        String originUrl = urlService.getOriginUrl(urlName);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originUrl))
                .build();
    }

    @GetMapping("/{urlName}/qrcode")
    public ResponseEntity<byte[]> qrCode(@PathVariable String urlName) throws Exception {

        String shortUrl = baseUrl + "/" + urlName;

        byte[] qrCode = qrCodeService.generate(shortUrl);

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/png")
                .body(qrCode);
    }

    @GetMapping("/{urlName}/qrcode/download")
    public ResponseEntity<byte[]> downloadQr(@PathVariable String urlName) throws Exception {

        String shortUrl = baseUrl + "/" + urlName;

        byte[] qrCode = qrCodeService.generate(shortUrl);

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/png")
                .header("Content-Disposition", "attachment; filename=\"qrcode-" + urlName + ".png\"")
                .body(qrCode);
    }
}