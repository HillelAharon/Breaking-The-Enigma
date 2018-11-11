package app.utils;


import java.util.Date;

public class MessagesUtils {
    Object payload;
    Date timeStamp;
    boolean status;
    public MessagesUtils(Object msg, boolean msg_status) {
        this.payload = msg;
        this.status = msg_status;
        this.timeStamp = new Date();
    }
}
