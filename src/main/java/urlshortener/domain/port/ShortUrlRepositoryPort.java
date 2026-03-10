package urlshortener.domain.port;

import urlshortener.domain.model.ShortUrl;

import java.util.Optional;

public interface ShortUrlRepositoryPort {

    /** Persists a short URL entity. */
    ShortUrl save(ShortUrl shortUrl);

    /** Finds a short URL by its short code. */
    Optional<ShortUrl> findByShortCode(String code);
}