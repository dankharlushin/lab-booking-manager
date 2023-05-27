package ru.github.dankharlushin.lbmlib.shell.service.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;
import ru.github.dankharlushin.lbmlib.shell.executor.CommandLineExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ShellProcessServiceImpl implements ShellProcessService {

    private static final Logger logger = LoggerFactory.getLogger(ShellProcessServiceImpl.class);

    private final CommandLineExecutor executor;

    public ShellProcessServiceImpl(final CommandLineExecutor executor) {
        this.executor = executor;
    }

    @Override
    public List<Integer> getPidsByCommand(final String command) {
        try (final InputStream pgrep = executor.pgrep(command, Map.of("-f", ""))) {
            final byte[] output = pgrep.readAllBytes();
            if (output.length > 0) {
                final String strPgrepOutput = new String(output);//fixme check empty case
                return Arrays.stream(strPgrepOutput.split("\n")).map(Integer::parseInt).toList();
            }
            return new ArrayList<>();
        } catch (final ExecutionException | IOException e) {
            logger.error("Unable to get PIDs by command [{}]", command, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<String> getUserByPid(final int pid) {
        try (final InputStream pgrep = executor.ps(Map.of("-p", String.valueOf(pid), "-o", "user", "--no-header", ""))) {
            final byte[] output = pgrep.readAllBytes();
            return output.length > 0 ? Optional.of(new String(output).replace(System.getProperty("line.separator"), ""))
                    : Optional.empty();//fixme check empty case
        } catch (final ExecutionException | IOException e) {
            logger.error("Unable to get user by PID [{}]", pid, e);
            return Optional.empty();
        }
    }

    @Override
    public void killByPid(final int pid) {
        try {
            final int exitValue = executor.kill(pid);
            if (exitValue != 0) {
                logger.error("Unable to kill process with PID [{}], exit value [{}]", pid, exitValue);
            }
        } catch (ExecutionException e) {
            logger.error("Unable to kill process with PID [{}]", pid);
        }
    }
}
