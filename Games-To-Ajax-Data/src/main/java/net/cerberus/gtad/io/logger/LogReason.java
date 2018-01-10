package net.cerberus.gtad.io.logger;

public enum LogReason {

    GTAD("GTAD"),
    CACHE("Cache"),
    DB("DB"),
    PARSER("Parser"),
    RIOT_API("Riot-APi");

    private String s;

    LogReason(String s) {
        this.s = s;
    }

    public String getName() {
        return s;
    }
}
