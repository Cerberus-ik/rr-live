package net.cerberus.sg.logger;

public enum LogLevel {

    INFO("Info"),
    ERROR("Error"),
    WARNING("Warning");

    private String s;

    LogLevel(String s) {
        this.s = s;
    }

    public String getName() {
        return s;
    }
}
