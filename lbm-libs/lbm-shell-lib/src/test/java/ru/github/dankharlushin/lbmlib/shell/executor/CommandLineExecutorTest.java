package ru.github.dankharlushin.lbmlib.shell.executor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.github.dankharlushin.lbmlib.shell.exception.ExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class CommandLineExecutorTest {

    private static final String TEST_PROCESS_COMMAND = "sleep";
    private static final String TEST_PROCESS_COMMAND_ARG = "30";

    private CommandLineExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new CommandLineExecutorImpl();

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

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        ProcessBuilder pgrep = new ProcessBuilder("pgrep", TEST_PROCESS_COMMAND);
        Process pgrepProcess = pgrep.start();
        pgrepProcess.waitFor();
        String pid = new String(pgrepProcess.getInputStream().readAllBytes()).trim();
        ProcessBuilder kill = new ProcessBuilder("kill", pid);
        Process killProcess = kill.start();
        killProcess.waitFor();
    }

    @Test
    void testPgrepSimple() throws IOException, ExecutionException {
        final InputStream pgrep = executor.pgrep(TEST_PROCESS_COMMAND, null);

        assertThat(pgrep, notNullValue());
        assertThat(pgrep.available() > 0, is(true));
    }

    @Test
    void testPgrepOption() throws ExecutionException, IOException {
        final Map<String, String> options = Map.of("-i", "", "-d", "!");
        final InputStream pgrep = executor.pgrep(TEST_PROCESS_COMMAND.toUpperCase(), options);

        assertThat(pgrep, notNullValue());
        assertThat(pgrep.available() > 0, is(true));
    }

    @Test
    void testPgrepEmptyOutput() throws ExecutionException, IOException {
        final InputStream pgrep = executor.pgrep("nonExistent", Map.of());

        assertThat(pgrep, notNullValue());
        assertThat(pgrep.available(), is(0));
    }

    @Test
    void testPsSimple() throws ExecutionException, IOException {
        final InputStream ps = executor.ps(null);

        assertThat(ps, notNullValue());
        assertThat(ps.available() > 0, is(true));
    }

    @Test
    void testPsOptions() throws ExecutionException, IOException {
        final InputStream ps = executor.ps(Map.of("-o", "command", "--no-header", ""));

        assertThat(ps, notNullValue());
        assertThat(ps.available() > 0, is(true));
    }

    @Test
    void testPsEmptyOutput() throws ExecutionException, IOException {
        final InputStream ps = executor.ps(Map.of("-p", "777", "--no-header", ""));

        assertThat(ps, notNullValue());
        assertThat(ps.available(), is(0));
    }

    @Test
    void testKillExistent() throws ExecutionException, IOException {
        final InputStream pgrep = executor.pgrep(TEST_PROCESS_COMMAND, Map.of());
        final String pid = new String(pgrep.readAllBytes());
        final int kill = executor.kill(Integer.parseInt(pid.replace("\n", "")));

        assertThat(kill, is(0));
    }

    @Test
    void testKillNonExistent() throws ExecutionException {
        final int kill = executor.kill(777);

        assertThat(kill != 0, is(true));
    }

    @Test
    void testNotifySendSimple() throws ExecutionException {
        final int notifySend = executor.notifySend("This is summary", "This is body", null, null);

        assertThat(notifySend, is(0));
    }

    @Test
    void testNotifySendOptions() throws ExecutionException {
        final int notifySend = executor.notifySend("This is summary", "This is body", Map.of("-u", "low"), null);

        assertThat(notifySend, is(0));
    }
}
