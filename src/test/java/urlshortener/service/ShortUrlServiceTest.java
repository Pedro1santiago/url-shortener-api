package urlshortener.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import urlshortener.application.service.ShortUrlService;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.domain.model.ShortUrl;
import urlshortener.domain.port.ShortUrlRepositoryPort;
import urlshortener.infrastructure.util.ShortCodeGenerator;
import urlshortener.validation.Url;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepositoryPort shortUrlRepositoryPort;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

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
}
