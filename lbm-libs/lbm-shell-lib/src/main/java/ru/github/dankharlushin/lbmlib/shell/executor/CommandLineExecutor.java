package ru.github.dankharlushin.lbmlib.shell.executor;

import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface CommandLineExecutor {

    InputStream pgrep(final String pattern, final Map<String, String> options) throws ExecutionException;

    InputStream ps(final Map<String, String> options) throws ExecutionException;

    InputStream ls(final String file, final Map<String, String> options) throws ExecutionException;

    InputStream id(final String user, final Map<String, String> options) throws ExecutionException;

    int kill(final int pid) throws ExecutionException;

    int chmod(final int permissions,
              final String file,
              final Map<String, String> options,
              final List<Map.Entry<String, String>> preExecutionOptions) throws ExecutionException;

    int chgrp(final String group,
              final String file,
              final Map<String, String> options,
              final List<Map.Entry<String, String>> preExecutionOptions) throws ExecutionException;

    int notifySend(final String summary,
                   final String body,
                   final Map<String, String> options,
                   final List<Map.Entry<String, String>> preExecutionOptions) throws ExecutionException;
}