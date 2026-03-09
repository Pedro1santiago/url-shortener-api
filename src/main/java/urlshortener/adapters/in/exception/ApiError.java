package urlshortener.adapters.in.exception;

public record ApiError(
        int status,
        String message,
        java.time.LocalDateTime timestamp
) {}
