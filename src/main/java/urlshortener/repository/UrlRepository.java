package urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import urlshortener.model.UrlShortener;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlShortener, Long> {

    Optional<UrlShortener> findByUrlName(String urlName);

    Optional<UrlShortener> findByOriginalUrl(String originalUrl);
}
