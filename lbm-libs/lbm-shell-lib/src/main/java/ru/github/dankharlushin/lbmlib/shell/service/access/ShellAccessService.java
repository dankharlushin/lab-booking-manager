package ru.github.dankharlushin.lbmlib.shell.service.access;

import ru.github.dankharlushin.lbmlib.shell.exception.ShellException;

public interface ShellAccessService {

    String getCurrentFileHolder(final String file) throws ShellException;

    void grantAccessToUser(final String file, final String user) throws ShellException;

    void setSafePermissions(final String file);
}
