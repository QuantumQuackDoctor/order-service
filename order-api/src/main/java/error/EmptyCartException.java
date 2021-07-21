package error;

public class EmptyCartException extends Exception{
    public EmptyCartException(String message){
        super(message);
    }
}
