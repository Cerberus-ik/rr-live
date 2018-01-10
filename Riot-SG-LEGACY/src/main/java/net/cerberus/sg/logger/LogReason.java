package net.cerberus.sg.logger;

public enum LogReason {

    CACHE("Cache"),
    SG("SG"),
    RIOT_API("Riot-APi");

    private String s;

    LogReason(String s) {
        this.s = s;
    }

    public String getName() {
        return s;
    }
}
