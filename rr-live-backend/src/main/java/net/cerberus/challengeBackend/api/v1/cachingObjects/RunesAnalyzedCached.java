package net.cerberus.challengeBackend.api.v1.cachingObjects;

import net.cerberus.challengeBackend.api.CachingObject;

import java.util.concurrent.TimeUnit;

public class RunesAnalyzedCached implements CachingObject {

    private long analyzedRunes;
    private long nanoTime;

    public RunesAnalyzedCached(long analyzedRunes) {
        this.analyzedRunes = analyzedRunes;
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

    public long getAnalyzedRunes() {
        return this.analyzedRunes;
    }
}
