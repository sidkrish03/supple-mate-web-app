package com.supplemateservice.exceptions;

public class InvalidUsernameException extends Throwable{
    public InvalidUsernameException(String message){
        super(message);
    }

    public InvalidUsernameException(String message, Throwable e){
        super(message, e);
    }
}
