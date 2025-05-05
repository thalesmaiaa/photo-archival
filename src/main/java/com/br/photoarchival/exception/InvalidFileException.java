package com.br.photoarchival.exception;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException() {
        super("Your request contains an invalid file");
    }
}
