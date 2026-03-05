package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.exception.CustomShortCodeBlankException;
import urlshortener.exception.InvalidUrlException;
import urlshortener.exception.ShortCodeAlreadyExistsException;
import urlshortener.exception.ShortCodeNotFoundException;
import urlshortener.model.ShortUrl;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.util.ShortCodeGenerator;

import java.util.Optional;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public void validateUrl(String url) {

        if (url == null || url.isBlank()) {
            throw new InvalidUrlException();
        }

        int httpSchemeIndex = url.indexOf("http://");
        int httpsSchemeIndex = url.indexOf("https://");
        int dotIndex = url.lastIndexOf(".");

        if ((httpSchemeIndex == -1 && httpsSchemeIndex == -1)
                || dotIndex == -1
                || dotIndex == url.length() - 1) {

            throw new InvalidUrlException();
        }
    }

    private String generateUniqueCode() {

        String code;

        do {
            code = ShortCodeGenerator.generateCode(5);
        } while (shortUrlRepository.findByShortCode(code).isPresent());

        return code;
    }

    public String createRandomShortCode(CreateShortUrlRequest request) {

        validateUrl(request.originalUrl());

        Optional<ShortUrl> existing =
                shortUrlRepository.findByOriginalUrl(request.originalUrl());

        ShortUrl entity = new ShortUrl();

        if (existing.isPresent()) {
            entity.setOriginalUrl(existing.get().getOriginalUrl());
        } else {
            entity.setOriginalUrl(request.originalUrl());
        }

        entity.setShortCode(generateUniqueCode());

        ShortUrl saved = shortUrlRepository.save(entity);

        return saved.getShortCode();
    }

    public String createCustomShortCode(CreateShortUrlRequest request) {

        if (request.customShortCode() == null || request.customShortCode().isBlank()) {
            throw new CustomShortCodeBlankException();
        }

        validateUrl(request.originalUrl());

        if (shortUrlRepository.findByShortCode(request.customShortCode()).isPresent()) {
            throw new ShortCodeAlreadyExistsException();
        }

        ShortUrl entity = new ShortUrl();
        entity.setOriginalUrl(request.originalUrl());
        entity.setShortCode(request.customShortCode());

        ShortUrl saved = shortUrlRepository.save(entity);

        return saved.getShortCode();
    }

    public String getOriginalUrl(String shortCode) {

        ShortUrl url = shortUrlRepository
                .findByShortCode(shortCode)
                .orElseThrow(ShortCodeNotFoundException::new);

        return url.getOriginalUrl();
    }
}
