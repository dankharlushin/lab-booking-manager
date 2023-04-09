package ru.github.dankharlushin.lbmlib.shell.exception;

public class ExecutionException extends Exception {

    public ExecutionException(final String message) {
        super(message);
    }

    public ExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
