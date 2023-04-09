package ru.github.dankharlushin.lbmlib.shell.service.process;

import java.util.List;
import java.util.Optional;

public interface ShellProcessService {

    List<Integer> getPidsByCommand(final String command);

    Optional<String> getUserByPid(final int pid);

    void killByPid(final int pid);
}
