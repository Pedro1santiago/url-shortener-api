package urlshortener.application.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.exception.CustomShortCodeBlankException;
import urlshortener.exception.ShortCodeAlreadyExistsException;
import urlshortener.exception.ShortCodeNotFoundException;
import urlshortener.domain.model.ShortUrl;
import urlshortener.domain.port.ShortUrlRepositoryPort;
import urlshortener.util.Base62;
import urlshortener.util.ShortCodeGenerator;
import urlshortener.validation.Url;

@Service
public class ShortUrlService {

    private final ShortUrlRepositoryPort repository;
    private final ShortCodeGenerator generator;
    private final RedisService redisService;


    public ShortUrlService(ShortUrlRepositoryPort repository, ShortCodeGenerator generator, RedisService redisService) {
        this.repository = repository;
        this.generator = generator;
        this.redisService = redisService;
    }

    private String generateUniqueCode() {

        String code;

        do {
            code = generator.generateCode(6);
        } while (repository.findByShortCode(code).isPresent());

        return code;
    }

    public String createRandomShortCode(CreateShortUrlRequest request) {

        Url.validate(request.originalUrl());

        ShortUrl entity = new ShortUrl();
        entity.setOriginalUrl(request.originalUrl());

        ShortUrl saved = repository.save(entity);

        String code = Base62.encode(saved.getId());

        saved.setShortCode(code);

        repository.save(saved);

        return code;
    }

    public String createCustomShortCode(CreateShortUrlRequest request) {

        if (request.customShortCode() == null || request.customShortCode().isBlank()) {
            throw new CustomShortCodeBlankException();
        }

        Url.validate(request.originalUrl());

        String code = request.customShortCode()
                .trim()
                .toLowerCase();

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

    public Long getClicks(String code) {
        return redisService.getClicks(code);
    }
}