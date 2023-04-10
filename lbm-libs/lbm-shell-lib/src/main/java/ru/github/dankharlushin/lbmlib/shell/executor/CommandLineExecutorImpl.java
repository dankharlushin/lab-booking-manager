package ru.github.dankharlushin.lbmlib.shell.executor;

import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CommandLineExecutorImpl implements CommandLineExecutor {

    private static final String PGREP_COMMAND = "pgrep";
    private static final String PS_COMMAND = "ps";
    private static final String KILL_COMMAND = "kill";
    private static final String NOTIFY_SEND_COMMAND = "notify-send";

    @Override
    public InputStream pgrep(final String pattern, final Map<String, Object> options) throws ExecutionException {
        final String commandArgs = argsToString(Optional.of(pattern), Optional.empty(), options);
        final Process process = execute(PGREP_COMMAND, commandArgs);
        return process.getInputStream();
    }

    @Override
    public InputStream ps(final Map<String, Object> options) throws ExecutionException {
        final String commandArgs = argsToString(Optional.empty(), Optional.empty(), options);
        final Process process = execute(PS_COMMAND, commandArgs);
        return process.getInputStream();
    }

    @Override
    public int kill(final int pid) throws ExecutionException {
        final Process process = execute(KILL_COMMAND, String.valueOf(pid));
        return process.exitValue();
    }

    @Override
    public int notifySend(final String prefix, final String summary, final String body, final Map<String, Object> options) throws ExecutionException {
        final String commandArgs = argsToString(Optional.empty(), Optional.of(summary + body), options);
        final Process process = execute(prefix + NOTIFY_SEND_COMMAND, commandArgs);
        return process.exitValue();
    }

    private String argsToString(final Optional<String> argsInPrefix,
                                final Optional<String> argsInSuffix,
                                final Map<String, Object> options) {
        if (options == null) {
            return argsInPrefix.orElse("") + argsInSuffix.orElse("");
        }
        return options
                .keySet()
                .stream()
                .map(key -> key + " " + options.get(key))
                .collect(Collectors.joining(" ", argsInPrefix.orElse("") + " ", argsInSuffix.orElse("")));
    }

    private Process execute(final String command, final String commandArgs) throws ExecutionException {
        final ProcessBuilder processBuilder = new ProcessBuilder(command, commandArgs);
        try {
            final Process process = processBuilder.start();
            process.waitFor();
            return process;
        } catch (final Exception e) {
            throw new ExecutionException("Unable to execute command [" + command + " " + commandArgs + "]", e);
        }
    }
}
