package urlshortener.validation;

import org.junit.jupiter.api.Test;
import urlshortener.exception.InvalidUrlException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlTest {

    @Test
    void validateShouldAcceptHttpAndHttps() {
        assertDoesNotThrow(() -> Url.validate("http://example.com"));
        assertDoesNotThrow(() -> Url.validate("https://example.com"));
    }

    @Test
    void validateShouldRejectNullOrBlank() {
        assertThrows(InvalidUrlException.class, () -> Url.validate(null));
        assertThrows(InvalidUrlException.class, () -> Url.validate(""));
        assertThrows(InvalidUrlException.class, () -> Url.validate("   "));
    }

    @Test
    void validateShouldRejectNonHttpScheme() {
        assertThrows(InvalidUrlException.class, () -> Url.validate("ftp://example.com"));
        assertThrows(InvalidUrlException.class, () -> Url.validate("file:///etc/passwd"));
    }

    @Test
    void validateShouldRejectMalformedUrl() {
        assertThrows(InvalidUrlException.class, () -> Url.validate("https://"));
        assertThrows(InvalidUrlException.class, () -> Url.validate("https://exa mple.com"));
    }
}
