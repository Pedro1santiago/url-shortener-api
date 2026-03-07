package urlshortener.adapters.persistence;

import org.springframework.stereotype.Component;
import urlshortener.domain.model.ShortUrl;
import urlshortener.domain.port.ShortUrlRepositoryPort;

import java.util.Optional;

@Component
public class ShortUrlRepositoryAdapter implements ShortUrlRepositoryPort {

    private final ShortUrlRepository repository;

    public ShortUrlRepositoryAdapter(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Override
    public ShortUrl save(ShortUrl shortUrl) {
        return repository.save(shortUrl);
    }

    @Override
    public Optional<ShortUrl> findByShortCode(String code) {
        return repository.findByShortCode(code);
    }
}