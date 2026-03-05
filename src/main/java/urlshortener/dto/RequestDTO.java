package urlshortener.dto;

public record RequestDTO(
        String originalUrl,
        String urlName
) {

}
