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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepositoryPort shortUrlRepositoryPort;

    @InjectMocks
    private ShortUrlService shortUrlService;

    @Test
    void shouldValidateUrlSuccessfully() {

        String url = "https://youtube.com";

        assertDoesNotThrow(() -> shortUrlService.validateUrl(url));
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
}
