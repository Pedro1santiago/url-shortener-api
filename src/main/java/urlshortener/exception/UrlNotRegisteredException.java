package urlshortener.exception;

public class UrlNotRegisteredException extends RuntimeException{

    public UrlNotRegisteredException(){
        super("Url not registered");
    }
}
