package com.gokul.help_desk_api.exception;

public class UnauthorizedTicketAccessException
        extends RuntimeException {

    public UnauthorizedTicketAccessException(String message) {
        super(message);
    }
}