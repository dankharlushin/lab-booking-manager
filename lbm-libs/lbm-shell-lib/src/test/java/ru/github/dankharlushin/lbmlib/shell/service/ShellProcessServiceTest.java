package ru.github.dankharlushin.lbmlib.shell.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.github.dankharlushin.lbmlib.shell.executor.CommandLineExecutorImpl;
import ru.github.dankharlushin.lbmlib.shell.service.process.ShellProcessService;
import ru.github.dankharlushin.lbmlib.shell.service.process.ShellProcessServiceImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ShellProcessServiceTest {

    private static final String TEST_PROCESS_COMMAND = "sleep";
    private static final String TEST_PROCESS_COMMAND_ARG = "300";

    private ShellProcessService shellProcessService;

    @BeforeEach
    void setUp() {
        shellProcessService = new ShellProcessServiceImpl(new CommandLineExecutorImpl());
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        ProcessBuilder pgrep = new ProcessBuilder("pgrep", TEST_PROCESS_COMMAND);
        Process pgrepProcess = pgrep.start();
        pgrepProcess.waitFor();
        String pids = new String(pgrepProcess.getInputStream().readAllBytes());
        Arrays.stream(pids.split("\n")).forEach(pid -> {
            try {
                ProcessBuilder kill = new ProcessBuilder("kill", pid);
                Process killProcess = kill.start();
                killProcess.waitFor();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testGetPidsByCommandSimple() {
        runTestProcess();
        runTestProcess();

        final List<Integer> pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);
        assertThat(pidsByCommand.size(), is(2));
    }

    @Test
    void testGetPidsByCommandNonExistent() {
        final List<Integer> pidsByCommand = shellProcessService.getPidsByCommand("nonExistent");
        assertThat(pidsByCommand.size(), is(0));
    }

    @Test
    void testGetUserByPidSimple() throws IOException, InterruptedException {
        runTestProcess();
        final ProcessBuilder processBuilder = new ProcessBuilder("whoami");
        final Process whoami = processBuilder.start();
        whoami.waitFor();
        final String user = new String(whoami.getInputStream().readAllBytes()).replace("\n", "");

        final List<Integer> pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);
        final Optional<String> userByPid = shellProcessService.getUserByPid(pidsByCommand.get(0));

        assertThat(userByPid.isPresent(), is(true));
        assertThat(userByPid.get(), is(user));
    }

    @Test
    void testGetUserByPidNonExistent() {
        final Optional<String> userByPid = shellProcessService.getUserByPid(777);

        assertThat(userByPid.isEmpty(), is(true));
    }

    @Test
    void testKillByPidSimple() {
        runTestProcess();
        List<Integer> pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);
        shellProcessService.killByPid(pidsByCommand.get(0));
        pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);

        assertThat(pidsByCommand.size(), is(0));
    }

    @Test
    void testKillByPidNonExistent() {
        runTestProcess();
        shellProcessService.killByPid(777);
        final List<Integer> pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);

        assertThat(pidsByCommand.size(), is(1));
    }

    private static void runTestProcess() {
        new Thread(() -> {
            final ProcessBuilder sleep = new ProcessBuilder(TEST_PROCESS_COMMAND, TEST_PROCESS_COMMAND_ARG);
            final Process sleepProcess;
            try {
                sleepProcess = sleep.start();
                sleepProcess.waitFor();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
