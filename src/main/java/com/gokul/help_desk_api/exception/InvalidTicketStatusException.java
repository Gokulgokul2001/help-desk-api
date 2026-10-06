package com.gokul.help_desk_api.exception;

public class InvalidTicketStatusException extends RuntimeException {

    public InvalidTicketStatusException(String message){
        super(message);
    }
}
