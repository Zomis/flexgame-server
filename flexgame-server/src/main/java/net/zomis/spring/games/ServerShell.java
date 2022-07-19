package net.zomis.spring.games;

import org.apache.log4j.Logger;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ServerShell {

    @ShellMethod("Get log level")
    public String getlog(String logger) {
        Logger log = Logger.getLogger(logger);
        return log.getName() + " has loglevel " + log.getLevel();
    }

    @ShellMethod("Change log level")
    public String setlog(String logger, String logLevel) {
        Logger log = Logger.getLogger(logger);
        log.setLevel(org.apache.log4j.Level.toLevel(logLevel));
        return log.getName() + " now has loglevel " + log.getLevel();
    }

}
