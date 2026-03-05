package urlshortener.exception;

public class UrlAlreadyRegistered extends IllegalArgumentException{

    public UrlAlreadyRegistered(){
        super("Url already registered");
    }
}
