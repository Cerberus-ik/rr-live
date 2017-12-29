package net.cerberus.challengeBackend.api.v1.cachingObjects;

import java.util.HashMap;

public class MostDominantRunesPerTimeId {

    private HashMap<String, RoleRune> runes;
    private int timeId;

    public MostDominantRunesPerTimeId(HashMap<String, RoleRune> runes, int timeId) {
        this.runes = runes;
        this.timeId = timeId;
    }

    public HashMap<String, RoleRune> roleRunes() {
        return this.runes;
    }

    public int getTimeId() {
        return this.timeId;
    }

}

