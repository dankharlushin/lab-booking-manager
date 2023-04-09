package ru.github.dankharlushin.lbmlib.shell.executor;

import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;

import java.io.InputStream;
import java.util.Map;

public interface CommandLineExecutor {

    InputStream pgrep(final String pattern, final Map<String, Object> options) throws ExecutionException;

    InputStream ps(final Map<String, Object> options) throws ExecutionException;

    int kill(final int pid) throws ExecutionException;

    int notifySend(final String prefix, final String summary, final String body, final Map<String, Object> options) throws ExecutionException;
}
