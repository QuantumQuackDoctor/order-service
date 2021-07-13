package error;
public class MissingFieldsException extends Exception{
    public MissingFieldsException (String errMessage){
        super (errMessage);
    }
}
