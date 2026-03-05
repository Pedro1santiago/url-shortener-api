package urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urlshortener.model.ShortUrl;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    Optional<ShortUrl> findByOriginalUrl(String originalUrl);
}
