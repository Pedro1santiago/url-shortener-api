package urlshortener.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import urlshortener.application.service.RedisService;
import urlshortener.application.service.ShortUrlService;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.domain.model.ShortUrl;
import urlshortener.domain.port.ShortUrlRepositoryPort;
import urlshortener.exception.CustomShortCodeBlankException;
import urlshortener.exception.InvalidUrlException;
import urlshortener.exception.ShortCodeAlreadyExistsException;
import urlshortener.exception.ShortCodeNotFoundException;
import urlshortener.infrastructure.util.ShortCodeGenerator;
import urlshortener.validation.Url;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepositoryPort shortUrlRepositoryPort;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private ShortUrlService shortUrlService;

    @Test
    void shouldValidateUrlSuccessfully() {

        String url = "https://youtube.com";

        assertDoesNotThrow(() -> Url.validate(url));
    }

    @Test
    void shouldCreateUrlCustom(){

        Long id = 1L;

        CreateShortUrlRequest dto = new CreateShortUrlRequest(
            "https://youtube.com",
            "MyVideo"
        );

        ShortUrl savedUrl = new ShortUrl();
        savedUrl.setId(id);
        savedUrl.setOriginalUrl("https://youtube.com");
        savedUrl.setShortCode("myvideo");

        when(shortUrlRepositoryPort.save(any(ShortUrl.class)))
                .thenReturn(savedUrl);

        String result = shortUrlService.createCustomShortCode(dto);

        assertEquals("myvideo", result);

        verify(shortUrlRepositoryPort).save(any(ShortUrl.class));

        verify(shortUrlRepositoryPort).save(argThat(url ->
            url.getShortCode().equals("myvideo")
        ));

    }

        @Test
        void shouldThrowWhenCustomCodeBlank() {

        CreateShortUrlRequest dto = new CreateShortUrlRequest(
            "https://youtube.com",
            "   "
        );

        assertThrows(CustomShortCodeBlankException.class,
            () -> shortUrlService.createCustomShortCode(dto));
        }

        @Test
        void shouldThrowWhenUrlInvalid() {

        CreateShortUrlRequest dto = new CreateShortUrlRequest(
            "ftp://example.com",
            null
        );

        assertThrows(InvalidUrlException.class,
            () -> shortUrlService.createRandomShortCode(dto));
        }

        @Test
        void shouldNormalizeCustomShortCode() {

        CreateShortUrlRequest dto = new CreateShortUrlRequest(
            "https://example.com",
            " My Video "
        );

        ShortUrl saved = new ShortUrl();
        saved.setId(1L);
        saved.setOriginalUrl(dto.originalUrl());
        saved.setShortCode("myvideo");

        when(shortUrlRepositoryPort.save(any(ShortUrl.class)))
            .thenReturn(saved);

        String result = shortUrlService.createCustomShortCode(dto);

        assertEquals("myvideo", result);
        verify(shortUrlRepositoryPort).save(argThat(url ->
            url.getShortCode().equals("myvideo")
                && url.getOriginalUrl().equals(dto.originalUrl())
        ));
        }

        @Test
        void shouldThrowWhenCustomCodeAlreadyExists() {

        CreateShortUrlRequest dto = new CreateShortUrlRequest(
            "https://example.com",
            "myvideo"
        );

        when(shortUrlRepositoryPort.save(any(ShortUrl.class)))
            .thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(ShortCodeAlreadyExistsException.class,
            () -> shortUrlService.createCustomShortCode(dto));
        }

    @Test
    void shouldCreateRandomShortUrl() {

        Long id = 1L;

        String originalUrl = "https://youtube.com";

        CreateShortUrlRequest dto = new CreateShortUrlRequest(
            originalUrl,
                null
        );

        when(shortCodeGenerator.generateCode(5))
                .thenReturn("abc12");

        ShortUrl savedUrl = new ShortUrl();
        savedUrl.setId(id);
        savedUrl.setOriginalUrl(originalUrl);
        savedUrl.setShortCode("abc12");


        when(shortUrlRepositoryPort.save(any(ShortUrl.class)))
                .thenReturn(savedUrl);

        String result = shortUrlService.createRandomShortCode(dto);

        assertEquals("abc12",result);

        verify(shortUrlRepositoryPort).save(any(ShortUrl.class));
        verify(shortUrlRepositoryPort).save(argThat(url ->
                url.getOriginalUrl().equals(originalUrl)
        ));

    }

        @Test
        void shouldRetryOnRandomCodeCollision() {

        CreateShortUrlRequest dto = new CreateShortUrlRequest(
            "https://example.com",
            null
        );

        when(shortCodeGenerator.generateCode(5))
            .thenReturn("abc12")
            .thenReturn("def34");

        ShortUrl saved = new ShortUrl();
        saved.setId(1L);
        saved.setOriginalUrl(dto.originalUrl());
        saved.setShortCode("def34");

        when(shortUrlRepositoryPort.save(any(ShortUrl.class)))
            .thenThrow(new DataIntegrityViolationException("duplicate"))
            .thenReturn(saved);

        String result = shortUrlService.createRandomShortCode(dto);

        assertEquals("def34", result);
        verify(shortUrlRepositoryPort, times(2)).save(any(ShortUrl.class));
        }

        @Test
        void shouldReturnCachedUrlAndIncrementClicks() {

        when(redisService.getCachedUrl("abc12"))
            .thenReturn("https://cached.com");

        String result = shortUrlService.getOriginalUrl("abc12");

        assertEquals("https://cached.com", result);
        verify(redisService).incrementClicks("abc12");
        verify(shortUrlRepositoryPort, never()).findByShortCode(any());
        }

        @Test
        void shouldLoadFromRepositoryCacheAndIncrementClicks() {

        when(redisService.getCachedUrl("abc12"))
            .thenReturn(null);

        ShortUrl entity = new ShortUrl();
        entity.setId(1L);
        entity.setShortCode("abc12");
        entity.setOriginalUrl("https://db.com");

        when(shortUrlRepositoryPort.findByShortCode("abc12"))
            .thenReturn(Optional.of(entity));

        String result = shortUrlService.getOriginalUrl("abc12");

        assertEquals("https://db.com", result);
        verify(redisService).cacheUrl("abc12", "https://db.com");
        verify(redisService).incrementClicks("abc12");
        }

        @Test
        void shouldThrowWhenShortCodeNotFound() {

        when(redisService.getCachedUrl("missing"))
            .thenReturn(null);

        when(shortUrlRepositoryPort.findByShortCode("missing"))
            .thenReturn(Optional.empty());

        assertThrows(ShortCodeNotFoundException.class,
            () -> shortUrlService.getOriginalUrl("missing"));
        }
}
