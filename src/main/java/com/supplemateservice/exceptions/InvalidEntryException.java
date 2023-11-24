package com.supplemateservice.exceptions;

public class InvalidEntryException extends Throwable {
    public InvalidEntryException(String message){
        super(message);
    }

    public InvalidEntryException(String message, Throwable e){
        super(message, e);
    }
}
