package urlshortener.exception;

public class ShortCodeAlreadyExistsException extends IllegalArgumentException {

    public ShortCodeAlreadyExistsException() {
        super("Short code already exists");
    }
}
