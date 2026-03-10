package urlshortener.adapters.in.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import urlshortener.application.service.QrCodeService;
import urlshortener.application.service.ShortUrlService;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.dto.CreateShortUrlResponse;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortUrlControllerTest {

    @Mock
    private ShortUrlService shortUrlService;

    @Mock
    private QrCodeService qrCodeService;

    private ShortUrlController controller(String baseUrl) {
        return new ShortUrlController(shortUrlService, qrCodeService, baseUrl);
    }

    @Test
    void createCustomShortUrlShouldReturn201WithLocationAndBody() {

        ShortUrlController controller = controller("https://short.ly");

        CreateShortUrlRequest request = new CreateShortUrlRequest(
                "https://example.com",
                "MyCode"
        );

        when(shortUrlService.createCustomShortCode(request)).thenReturn("mycode");

        ResponseEntity<CreateShortUrlResponse> response = controller.createCustomShortUrl(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("https://short.ly/mycode"), response.getHeaders().getLocation());

        CreateShortUrlResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("https://short.ly/mycode", body.shortUrl());
        assertEquals("mycode", body.code());
        assertEquals("https://example.com", body.originalUrl());
    }

    @Test
    void createRandomShortUrlShouldReturn201WithLocationAndBody() {

        ShortUrlController controller = controller("https://short.ly");

        CreateShortUrlRequest request = new CreateShortUrlRequest(
                "https://example.com",
                null
        );

        when(shortUrlService.createRandomShortCode(request)).thenReturn("abc12");

        ResponseEntity<CreateShortUrlResponse> response = controller.createRandomShortUrl(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("https://short.ly/abc12"), response.getHeaders().getLocation());

        CreateShortUrlResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("https://short.ly/abc12", body.shortUrl());
        assertEquals("abc12", body.code());
        assertEquals("https://example.com", body.originalUrl());
    }

    @Test
    void redirectShouldReturn302WithLocationHeader() {

        ShortUrlController controller = controller("https://short.ly");

        when(shortUrlService.getOriginalUrl("abc12"))
                .thenReturn("https://example.com");

        ResponseEntity<Void> response = controller.redirectToOriginalUrl("abc12");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(URI.create("https://example.com"), response.getHeaders().getLocation());
    }

    @Test
    void qrCodeShouldReturnPngBytes() throws Exception {

        ShortUrlController controller = controller("https://short.ly");

        byte[] png = new byte[]{1, 2, 3};

        when(qrCodeService.generate("https://short.ly/abc12"))
                .thenReturn(png);

        ResponseEntity<byte[]> response = controller.qrCode("abc12");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("image/png", response.getHeaders().getFirst("Content-Type"));
        assertArrayEquals(png, response.getBody());
    }

    @Test
    void downloadQrCodeShouldReturnAttachmentWithFilename() throws Exception {

        ShortUrlController controller = controller("https://short.ly");

        byte[] png = new byte[]{1, 2, 3};

        when(qrCodeService.generate("https://short.ly/abc12"))
                .thenReturn(png);

        ResponseEntity<byte[]> response = controller.downloadQrCode("abc12");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("image/png", response.getHeaders().getFirst("Content-Type"));
        assertEquals("attachment; filename=\"qrcode-abc12.png\"", response.getHeaders().getFirst("Content-Disposition"));
        assertArrayEquals(png, response.getBody());
    }
}
