package com.supplemateservice.exceptions;

public class InvalidSupplementTypeException extends Throwable{
    public InvalidSupplementTypeException(String message){
        super(message);
    }

    public InvalidSupplementTypeException(String message, Throwable e){
        super(message, e);
    }
}
