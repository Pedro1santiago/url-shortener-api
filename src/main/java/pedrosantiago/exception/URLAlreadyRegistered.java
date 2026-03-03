package pedrosantiago.exception;

public class URLAlreadyRegistered extends IllegalArgumentException{

    public URLAlreadyRegistered(){
        super("URL already registered");
    }
}
