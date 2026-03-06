package urlshortener.dto;

public record CreateShortUrlResponse(
        String shortUrl,
        String code,
        String originalUrl
) {}