package com.mavi.wishlist.exceptions;

public class InvalidFieldsException extends RuntimeException {

    private final String incorrectField;

    public InvalidFieldsException(String message, String incorrectField) {
        super(message);
        this.incorrectField = incorrectField;
    }

    public String getIncorrectField() {
        return incorrectField;
    }
}
