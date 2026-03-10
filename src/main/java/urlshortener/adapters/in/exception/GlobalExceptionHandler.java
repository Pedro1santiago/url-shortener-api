package urlshortener.adapters.in.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Maps blank custom code errors to HTTP 400. */
    @ExceptionHandler(CustomShortCodeBlankException.class)
    public ResponseEntity<ApiError> handleCustomShortCodeBlankException(CustomShortCodeBlankException e) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Maps missing short code errors to HTTP 404. */
    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ApiError> handleShortCodeNotFound(ShortCodeNotFoundException e) {
        return buildError(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maps short code collision errors to HTTP 409. */
    @ExceptionHandler(ShortCodeAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleShortCodeAlreadyExistsException(ShortCodeAlreadyExistsException e) {
        return buildError(HttpStatus.CONFLICT, e.getMessage());
    }

    /** Maps invalid URL errors to HTTP 400. */
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ApiError> handleInvalidUrlException(InvalidUrlException e) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Creates a consistent error payload for API responses. */
    private ResponseEntity<ApiError> buildError(HttpStatus status, String message) {

        ApiError error = new ApiError(
                status.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }
}