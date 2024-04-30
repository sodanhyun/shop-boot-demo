package com.react.demo.constant;

import com.beust.jcommander.Parameter;

public class InitArgs {
    public static final String HELP = "--help";
    @Parameter(names = HELP, description = "Display usage information", help = true)
    private boolean help;

    public static final String DBHOST = "--dbhost";
    @Parameter(names = DBHOST, description = "The endpoint of Database", required = true)
    private String dbHost;

    public String getDbHost() {
        return dbHost;
    }

    public static final String DBPORT = "--dbport";
    @Parameter(names = DBPORT, description = "The port of Database", required = true)
    private String dbPort;

    public String getDbPort() {
        return dbPort;
    }

    public static final String USERNAME = "--username";
    @Parameter(names = USERNAME, description = "The username of Database", required = true)
    private String username;

    public String getUsername() {
        return username;
    }

    public static final String PASSWORD = "--password";
    @Parameter(names = PASSWORD, description = "The password of Database", required = true)
    private String password;

    public String getPassword() {
        return password;
    }
}
