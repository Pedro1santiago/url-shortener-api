package urlshortener.application.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.exception.CustomShortCodeBlankException;
import urlshortener.exception.ShortCodeAlreadyExistsException;
import urlshortener.exception.ShortCodeNotFoundException;
import urlshortener.domain.model.ShortUrl;
import urlshortener.domain.port.ShortUrlRepositoryPort;
import urlshortener.infrastructure.util.ShortCodeGenerator;
import urlshortener.validation.Url;

@Service
public class ShortUrlService {

    private static final int SHORT_CODE_LENGTH = 5;

    private final ShortUrlRepositoryPort repository;
    private final ShortCodeGenerator generator;
    private final RedisService redisService;

    public ShortUrlService(
            ShortUrlRepositoryPort repository,
            ShortCodeGenerator generator,
            RedisService redisService) {

        this.repository = repository;
        this.generator = generator;
        this.redisService = redisService;
    }

    /** Creates a new short URL using a random code and persists it. */
    @Transactional
    public String createRandomShortCode(CreateShortUrlRequest request) {

        Url.validate(request.originalUrl());

        while (true) {

            String code = generator.generateCode(SHORT_CODE_LENGTH);

            ShortUrl entity = new ShortUrl();
            entity.setOriginalUrl(request.originalUrl());
            entity.setShortCode(code);

            try {

                ShortUrl saved = repository.save(entity);
                return saved.getShortCode();

            } catch (DataIntegrityViolationException ignored) {
            }
        }
    }

    /** Creates a new short URL using a client-provided custom code. */
    @Transactional
    public String createCustomShortCode(CreateShortUrlRequest request) {

        if (request.customShortCode() == null || request.customShortCode().isBlank()) {
            throw new CustomShortCodeBlankException();
        }

        Url.validate(request.originalUrl());

        String code = request.customShortCode()
                .trim()
                .toLowerCase()
                .replace(" ", "");

        ShortUrl entity = new ShortUrl();
        entity.setOriginalUrl(request.originalUrl());
        entity.setShortCode(code);

        try {

            ShortUrl saved = repository.save(entity);
            return saved.getShortCode();

        } catch (DataIntegrityViolationException e) {

            throw new ShortCodeAlreadyExistsException();
        }
    }

    /** Resolves a short code to its original URL (with Redis cache + click counting). */
    @Transactional(readOnly = true )
    public String getOriginalUrl(String shortCode) {

        String cached = redisService.getCachedUrl(shortCode);

        if (cached != null) {
            redisService.incrementClicks(shortCode);
            return cached;
        }

        ShortUrl url = repository
                .findByShortCode(shortCode)
                .orElseThrow(ShortCodeNotFoundException::new);

        redisService.cacheUrl(shortCode, url.getOriginalUrl());

        redisService.incrementClicks(shortCode);

        return url.getOriginalUrl();
    }

    /** Returns the click counter for a short code from Redis. */
    @Transactional(readOnly = true )
    public Long getClicks(String code) {
        return redisService.getClicks(code);
    }
}