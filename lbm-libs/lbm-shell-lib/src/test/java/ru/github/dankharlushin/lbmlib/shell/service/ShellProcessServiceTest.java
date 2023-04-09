package ru.github.dankharlushin.lbmlib.shell.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.github.dankharlushin.lbmlib.shell.executor.CommandLineExecutorImpl;
import ru.github.dankharlushin.lbmlib.shell.service.process.ShellProcessService;
import ru.github.dankharlushin.lbmlib.shell.service.process.ShellProcessServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ShellProcessServiceTest {

    private static final String TEST_PROCESS_COMMAND = "sleep";
    private static final String TEST_PROCESS_COMMAND_ARG = "30";

    private ShellProcessService shellProcessService;

    @BeforeEach
    void setUp() {
        shellProcessService = new ShellProcessServiceImpl(new CommandLineExecutorImpl());
    }

    @Test
    void testGetPidsByCommandSimple() throws IOException, InterruptedException {
        final ProcessBuilder sleep = new ProcessBuilder(TEST_PROCESS_COMMAND, TEST_PROCESS_COMMAND_ARG);
        final Process sleepProcess = sleep.start();
        sleepProcess.waitFor();
        final ProcessBuilder sleep1 = new ProcessBuilder(TEST_PROCESS_COMMAND, TEST_PROCESS_COMMAND_ARG);//fixme use one process?
        final Process sleepProcess1 = sleep1.start();
        sleepProcess1.waitFor();

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
        final ProcessBuilder sleep = new ProcessBuilder(TEST_PROCESS_COMMAND, TEST_PROCESS_COMMAND_ARG);
        final Process sleepProcess = sleep.start();
        sleepProcess.waitFor();
        final List<Integer> pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);
        final Optional<String> userByPid = shellProcessService.getUserByPid(pidsByCommand.get(0));

        assertThat(userByPid.isPresent(), is(true));
        assertThat(userByPid.get(), is("root"));
    }

    @Test
    void testGetUserByPidNonExistent() {
        final Optional<String> userByPid = shellProcessService.getUserByPid(777);

        assertThat(userByPid.isEmpty(), is(true));
    }

    @Test
    void testKillByPidSimple() throws IOException, InterruptedException {
        final ProcessBuilder sleep = new ProcessBuilder(TEST_PROCESS_COMMAND, TEST_PROCESS_COMMAND_ARG);
        final Process sleepProcess = sleep.start();
        sleepProcess.waitFor();
        List<Integer> pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);
        shellProcessService.killByPid(pidsByCommand.get(0));
        pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);

        assertThat(pidsByCommand.size(), is(0));
    }

    @Test
    void testKillByPidNonExistent() throws IOException, InterruptedException {
        final ProcessBuilder sleep = new ProcessBuilder(TEST_PROCESS_COMMAND, TEST_PROCESS_COMMAND_ARG);
        final Process sleepProcess = sleep.start();
        sleepProcess.waitFor();
        shellProcessService.killByPid(777);
        final List<Integer> pidsByCommand = shellProcessService.getPidsByCommand(TEST_PROCESS_COMMAND);

        assertThat(pidsByCommand.size(), is(1));
    }
}
