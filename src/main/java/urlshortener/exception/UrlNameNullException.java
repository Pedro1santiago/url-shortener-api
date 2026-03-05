package urlshortener.exception;

public class UrlNameNullException extends IllegalArgumentException{

    public UrlNameNullException(){
        super("UrlName cannot null");
    }
}
