package net.cerberus.challengeBackend.api.v1.cachingObjects;

import net.cerberus.challengeBackend.api.CachingObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MostDominantRunesCache implements CachingObject {

    private long nanoTime;
    private List<MostDominantRunesPerTimeId> runes;

    public MostDominantRunesCache(List<MostDominantRunesPerTimeId> runes) {
        this.runes = runes;
        this.nanoTime = System.nanoTime();
    }

    public List<MostDominantRunesPerTimeId> getRunes() {
        return runes;
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


}
