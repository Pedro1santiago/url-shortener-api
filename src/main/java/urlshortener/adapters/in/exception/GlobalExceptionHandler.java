package urlshortener.adapters.in.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomShortCodeBlankException.class)
    public ResponseEntity<ApiError> handleCustomShortCodeBlankException(CustomShortCodeBlankException e) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ApiError> handleShortCodeNotFound(ShortCodeNotFoundException e) {
        return buildError(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ShortCodeAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleShortCodeAlreadyExistsException(ShortCodeAlreadyExistsException e) {
        return buildError(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ApiError> handleInvalidUrlException(InvalidUrlException e) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage());
    }

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