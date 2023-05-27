package ru.github.dankharlushin.lbmlib.shell.exception;

public class ShellException extends Exception {

    public ShellException(final String message) {
        super(message);
    }

    public ShellException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
