package net.cerberus.challengeBackend.api.v1.cachingObjects;

public class RoleRune {
    private String role;
    private int runeId;
    private long timesPicked;

    public RoleRune(String role, int runeId, long timesPicked) {
        this.role = role;
        this.runeId = runeId;
        this.timesPicked = timesPicked;
    }

    public String getRole() {
        return role;
    }

    public int getRuneId() {
        return runeId;
    }

    public long getTimesPicked() {
        return timesPicked;
    }
}
