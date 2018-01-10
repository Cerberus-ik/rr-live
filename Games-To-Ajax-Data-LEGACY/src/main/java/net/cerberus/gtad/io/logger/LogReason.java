package net.cerberus.gtad.io.logger;

public enum LogReason {

    CACHE("Cache"),
    DB("DB"),
    PARSER("Parser"),
    FILE_READER("File-Reader"),
    RIOT_API("Riot-APi");

    private String s;

    LogReason(String s) {
        this.s = s;
    }

    public String getName() {
        return s;
    }
}
