package urlshortener.dto;

public record CreateShortUrlRequest(
        String originalUrl,
        String customShortCode
) {

}
