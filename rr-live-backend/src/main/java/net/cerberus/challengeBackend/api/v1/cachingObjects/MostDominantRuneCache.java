package net.cerberus.challengeBackend.api.v1.cachingObjects;

import net.cerberus.challengeBackend.api.CachingObject;

import java.util.concurrent.TimeUnit;

public class MostDominantRuneCache implements CachingObject {

    private int timesPicked;
    private int totalRunesPicked;
    private int timeId;
    private int getRuneId;
    private String role;
    private long nanoTime;

    public MostDominantRuneCache(int timesPicked, int totalRunesPicked, int timeId, int getRuneId, String role) {
        this.timesPicked = timesPicked;
        this.totalRunesPicked = totalRunesPicked;
        this.timeId = timeId;
        this.getRuneId = getRuneId;
        this.role = role;
        this.nanoTime = System.nanoTime();
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.HOURS;
    }

    @Override
    public long getCachingTime() {
        return 12;
    }

    @Override
    public long getTimeStamp() {
        return this.nanoTime;
    }

    public long getTimesPicked() {
        return timesPicked;
    }

    public int getTimeId() {
        return timeId;
    }

    public int getRuneId() {
        return getRuneId;
    }

    public String getRole() {
        return role;
    }

    public int getTotalRunesPicked() {
        return totalRunesPicked;
    }
}
