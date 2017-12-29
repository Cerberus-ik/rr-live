package net.cerberus.challengeBackend.api.v1.cachingObjects;

import net.cerberus.challengeBackend.api.CachingObject;

import java.util.concurrent.TimeUnit;

public class LeastPickedRuneCache implements CachingObject {

    private int timesPicked;
    private int getRuneId;
    private long nanoTime;

    public LeastPickedRuneCache(int timesPicked, int getRuneId) {
        this.timesPicked = timesPicked;
        this.getRuneId = getRuneId;
        this.nanoTime = System.nanoTime();
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.HOURS;
    }

    @Override
    public long getCachingTime() {
        return 6;
    }

    @Override
    public long getTimeStamp() {
        return this.nanoTime;
    }

    public long getTimesPicked() {
        return timesPicked;
    }

    public int getRuneId() {
        return getRuneId;
    }

}
