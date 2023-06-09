package ru.github.dankharlushin.lbmlib.shell.executor;

import org.springframework.stereotype.Component;
import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;

import java.io.InputStream;
import java.util.*;

import static org.springframework.util.StringUtils.hasText;

@Component
public class CommandLineExecutorImpl implements CommandLineExecutor {

    private static final String PGREP_COMMAND = "pgrep";
    private static final String PS_COMMAND = "ps";
    private static final String KILL_COMMAND = "kill";
    private static final String NOTIFY_SEND_COMMAND = "notify-send";
    private static final String CHGRP_COMMAND = "chgrp";
    private static final String CHMOD_COMMAND = "chmod";
    private static final String LS_COMMAND = "ls";
    private static final String ID_COMMAND = "id";

    @Override
    public InputStream pgrep(final String pattern, final Map<String, String> options) throws ExecutionException {
        final List<String> command = new CommandBuilder()
                .withCommandName(PGREP_COMMAND)
                .withParameters(pattern)
                .withOptions(options)
                .build();
        final Process process = execute(command);
        return process.getInputStream();
    }

    @Override
    public InputStream ps(final Map<String, String> options) throws ExecutionException {
        final List<String> command = new CommandBuilder()
                .withCommandName(PS_COMMAND)
                .withOptions(options)
                .build();
        final Process process = execute(command);
        return process.getInputStream();
    }

    @Override
    public InputStream ls(final String file, final Map<String, String> options) throws ExecutionException {
        final List<String> command = new CommandBuilder()
                .withCommandName(LS_COMMAND)
                .withOptions(options)
                .build();
        final Process process = execute(command);
        return process.getInputStream();
    }

    @Override
    public InputStream id(final String user, final Map<String, String> options) throws ExecutionException {
        final List<String> command = new CommandBuilder()
                .withParameters(user)
                .withCommandName(ID_COMMAND)
                .withOptions(options)
                .build();
        final Process process = execute(command);
        return process.getInputStream();
    }

    @Override
    public int kill(final int pid) throws ExecutionException {
        final List<String> command = new CommandBuilder()
                .withCommandName(KILL_COMMAND)
                .withParameters(String.valueOf(pid))
                .build();
        final Process process = execute(command);
        return process.exitValue();
    }

    @Override
    public int chmod(final int permissions,
                     final String file,
                     final Map<String, String> options,
                     final List<Map.Entry<String, String>> preExecutionOptions) throws ExecutionException {
        final List<String> command = new CommandBuilder()
                .withCommandName(CHMOD_COMMAND)
                .withPreExecutionOptions(preExecutionOptions)
                .withParameters(String.valueOf(permissions), file)
                .withOptions(options)
                .build();
        final Process process = execute(command);
        return process.exitValue();
    }

    @Override
    public int chgrp(final String group,
                     final String file,
                     final Map<String, String> options,
                     final List<Map.Entry<String, String>> preExecutionOptions) throws ExecutionException {
        final List<String> command = new CommandBuilder()
                .withCommandName(CHGRP_COMMAND)
                .withPreExecutionOptions(preExecutionOptions)
                .withParameters(group, file)
                .withOptions(options)
                .build();
        final Process process = execute(command);
        return process.exitValue();
    }

    @Override
    public int notifySend(final String summary,
                          final String body,
                          final Map<String, String> options,
                          final List<Map.Entry<String, String>> preExecutionOptions) throws ExecutionException {
        final List<String> commandArgs = new CommandBuilder()
                .withCommandName(NOTIFY_SEND_COMMAND)
                .withPreExecutionOptions(preExecutionOptions)
                .withParameters(summary, body)
                .withOptions(options)
                .build();
        final Process process = execute(commandArgs);
        return process.exitValue();
    }

    private Process execute(final List<String> command) throws ExecutionException {
        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        try {
            final Process process = processBuilder.start();
            process.waitFor(); // todo check exit code?
            return process;
        } catch (final Exception e) {
            throw new ExecutionException("Unable to execute command [" + String.join(" ", command) + "]", e);
        }
    }

    private static class CommandBuilder {

        private String commandName;
        private String[] parameters;
        private Map<String, String> options;
        private List<Map.Entry<String, String>> preExecutionOptions;

        List<String> command;

        public CommandBuilder() {
            this.command = new ArrayList<>();
        }

        public CommandBuilder withCommandName(final String commandName) {
            this.commandName = commandName;
            return this;
        }

        public CommandBuilder withParameters(final String... parameters) {
            this.parameters = parameters;
            return this;
        }

        public CommandBuilder withPreExecutionOptions(final List<Map.Entry<String, String>> preExecutionOptions) {
            this.preExecutionOptions = preExecutionOptions;
            return this;
        }

        public CommandBuilder withOptions(final Map<String, String> options) {
            this.options = options;
            return this;
        }

        public List<String> build() {
            final List<String> cmd = new ArrayList<>();
            if (preExecutionOptions != null) { preExecutionOptions.forEach(opt -> addOptions(Map.ofEntries(opt), cmd)); }
            cmd.add(commandName);
            if (parameters != null) { cmd.addAll(Arrays.stream(parameters).filter(Objects::nonNull).toList()); }
            addOptions(options, cmd);

            return cmd;
        }

        private void addOptions(final Map<String, String> options, final List<String> cmd) {
            if (options != null && !options.isEmpty()) {
                for (Map.Entry<String, String> optEntry : options.entrySet()) {
                    addNotEmpty(optEntry.getKey(), cmd);
                    addNotEmpty(optEntry.getValue(), cmd);
                }
            }
        }

        private void addNotEmpty(final String parameter, final List<String> cmd) {
            if (hasText(parameter)) { cmd.add(parameter); }
        }
    }
}
