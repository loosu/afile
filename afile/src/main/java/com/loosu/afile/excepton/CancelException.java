package com.loosu.afile.excepton;

public class CancelException extends RuntimeException {
    public CancelException() {
    }

    public CancelException(String message) {
        super(message);
    }
}
