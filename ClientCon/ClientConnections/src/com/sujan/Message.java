package com.sujan;

/**
 * Created by sujan on 17/01/17.
 */
public class Message
{
    private int requestNumber = 0;
    private String message;

    public Message(String message) {
        this.message = message;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
