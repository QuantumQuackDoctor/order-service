package error;

import com.database.ormlibrary.order.OrderTimeEntity;

public class OrderTimeException extends Exception{
    public OrderTimeException (String message){
        super (message);
    }
}
