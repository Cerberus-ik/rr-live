package net.cerberus.sg.common;

import java.util.Arrays;

public enum Tier {

    BRONZE(1),
    SILVER(2),
    GOLD(3),
    PLATINUM(4),
    DIAMOND(5),
    MASTER(6),
    CHALLENGER(7),;

    private int level;

    Tier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Tier parseTierByName(String name) {
        return Arrays.stream(values()).filter((tier) -> tier.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isTierHigherThan(Tier tier){
        return this.level > tier.level;
    }
}
