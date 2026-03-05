package urlshortener.exception;

public class CustomShortCodeBlankException extends IllegalArgumentException {

    public CustomShortCodeBlankException() {
        super("Custom short code must not be blank");
    }
}
