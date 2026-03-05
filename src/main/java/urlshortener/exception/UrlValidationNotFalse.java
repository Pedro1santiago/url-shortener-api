package urlshortener.exception;

public class UrlValidationNotFalse extends RuntimeException{

    public UrlValidationNotFalse(){
        super("The domain is incorrect");
    }
}
