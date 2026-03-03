package pedrosantiago.exception;

public class URLValidationNotFalse extends RuntimeException{

    public URLValidationNotFalse(){
        super("The domain is incorrect");
    }
}
