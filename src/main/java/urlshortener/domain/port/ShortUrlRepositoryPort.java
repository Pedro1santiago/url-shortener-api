package urlshortener.domain.port;

import urlshortener.domain.model.ShortUrl;

import java.util.Optional;

public interface ShortUrlRepositoryPort {

    ShortUrl save(ShortUrl shortUrl);

    Optional<ShortUrl> findByShortCode(String code);
}