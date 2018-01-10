package net.cerberus.gg.io.logger;

public enum LogReason {

    GG("GG"),
    DB("DB"),
    RIOT_API("Riot-APi");

    private String s;

    LogReason(String s) {
        this.s = s;
    }

    public String getName() {
        return s;
    }
}
