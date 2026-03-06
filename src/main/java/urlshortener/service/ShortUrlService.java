package urlshortener.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import urlshortener.dto.CreateShortUrlRequest;
import urlshortener.exception.CustomShortCodeBlankException;
import urlshortener.exception.InvalidUrlException;
import urlshortener.exception.ShortCodeAlreadyExistsException;
import urlshortener.exception.ShortCodeNotFoundException;
import urlshortener.model.ShortUrl;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.util.ShortCodeGenerator;

import java.net.URL;

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

        try {

            URL parsed = new URL(url);

            if (!parsed.getProtocol().equals("http") &&
                    !parsed.getProtocol().equals("https")) {

                throw new InvalidUrlException();
            }

        } catch (Exception e) {
            throw new InvalidUrlException();
        }
    }

    private String generateUniqueCode() {

        String code;

        do {
            code = ShortCodeGenerator.generateCode(6);
        } while (shortUrlRepository.findByShortCode(code).isPresent());

        return code;
    }

    public String createRandomShortCode(CreateShortUrlRequest request) {

        validateUrl(request.originalUrl());

        ShortUrl entity = new ShortUrl();
        entity.setOriginalUrl(request.originalUrl());

        while (true) {
            try {

                entity.setShortCode(generateUniqueCode());

                ShortUrl saved = shortUrlRepository.save(entity);

                return saved.getShortCode();

            } catch (DataIntegrityViolationException e) {
            }
        }
    }

    public String createCustomShortCode(CreateShortUrlRequest request) {

        if (request.customShortCode() == null || request.customShortCode().isBlank()) {
            throw new CustomShortCodeBlankException();
        }

        validateUrl(request.originalUrl());

        String code = request.customShortCode()
                .trim()
                .toLowerCase();

        ShortUrl entity = new ShortUrl();
        entity.setOriginalUrl(request.originalUrl());
        entity.setShortCode(code);

        try {

            ShortUrl saved = shortUrlRepository.save(entity);

            return saved.getShortCode();

        } catch (DataIntegrityViolationException e) {

            throw new ShortCodeAlreadyExistsException();
        }
    }

    public String getOriginalUrl(String shortCode) {

        ShortUrl url = shortUrlRepository
                .findByShortCode(shortCode)
                .orElseThrow(ShortCodeNotFoundException::new);

        return url.getOriginalUrl();
    }
}