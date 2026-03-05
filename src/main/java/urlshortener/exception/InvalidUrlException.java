package urlshortener.exception;

public class InvalidUrlException extends IllegalArgumentException {

    public InvalidUrlException() {
        super("Invalid URL");
    }
}
