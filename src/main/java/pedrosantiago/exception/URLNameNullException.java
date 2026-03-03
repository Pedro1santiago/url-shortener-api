package pedrosantiago.exception;

public class URLNameNullException extends IllegalArgumentException{

    public URLNameNullException(){
        super("UrlName cannot null");
    }
}
