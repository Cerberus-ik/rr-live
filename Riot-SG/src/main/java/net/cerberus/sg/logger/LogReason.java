package net.cerberus.sg.logger;

public enum LogReason {

    CACHE("Cache"),
    SG("SG"),
    PARSER("Parser"),
    RIOT_API("Riot-APi"),
    RIOT_API_EXCEPTION_HANDLER("Riot-APi-Exception-Handler");

    private String s;

    LogReason(String s) {
        this.s = s;
    }

    public String getName() {
        return s;
    }
}
