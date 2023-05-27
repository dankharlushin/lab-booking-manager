package ru.github.dankharlushin.lbmlib.shell.service.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;
import ru.github.dankharlushin.lbmlib.shell.exception.ShellException;
import ru.github.dankharlushin.lbmlib.shell.executor.CommandLineExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.Collections.singletonList;

@Service
public class ShellAccessServiceImpl implements ShellAccessService {

    private static final int SAFE_PERMISSIONS = 710;
    private static final int HOLDER_POSITION = 3;
    private static final Logger logger = LoggerFactory.getLogger(ShellAccessServiceImpl.class);

    private final CommandLineExecutor executor;

    public ShellAccessServiceImpl(final CommandLineExecutor executor) {
        this.executor = executor;
    }

    @Override
    public String getCurrentFileHolder(final String file) throws ShellException {
        try (final InputStream ls = executor.ls(file, Map.of("-l", ""));) {
            final String commandOutput = new String(ls.readAllBytes());
            final String[] cols = commandOutput.split(" ");
            if (cols.length != 9) {
                throw new ShellException("Bad command output [" + commandOutput + "]");
            }
            return cols[HOLDER_POSITION];
        } catch (final ExecutionException | IOException e) {
            throw new ShellException("Unable to get holder for file [" + file + "]");
        }
    }

    @Override
    public void grantAccessToUser(final String file, final String user) throws ShellException {
        try {
            final List<Map.Entry<String, String>> preExecutionOptions = singletonList(new AbstractMap.SimpleEntry<>("sudo", ""));
            final int exitValue = executor.chgrp(user, file, Map.of(), preExecutionOptions);
            if (exitValue != 0) {
                throw new ShellException("Unable grant access to user [" + user + "] to file [" + file + "]");
            }
        } catch (final Exception e) {
            throw new ShellException("Unable grant access to user [" + user + "] to file [" + file + "]", e);
        }
    }

    @Override
    public void setSafePermissions(final String file) {
        try {
            final List<Map.Entry<String, String>> preExecutionOptions = singletonList(new AbstractMap.SimpleEntry<>("sudo", ""));
            final int exitValue = executor.chmod(SAFE_PERMISSIONS, file, Map.of(), preExecutionOptions);
            if (exitValue != 0) {
                logger.error("Unable to set safe permissions on file [{}]", file);
            }
        } catch (final ExecutionException e) {
            logger.error("Unable to set safe permissions on file [{}]", file, e);
        }
    }
}
