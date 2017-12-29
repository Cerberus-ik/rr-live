package net.cerberus.challengeBackend.api.v1.cachingServices;

import net.cerberus.challengeBackend.Main;
import net.cerberus.challengeBackend.api.CachingService;
import net.cerberus.challengeBackend.api.v1.cachingObjects.LeastPickedRuneCache;
import net.cerberus.challengeBackend.api.v1.cachingObjects.MostDominantRuneCache;
import net.cerberus.challengeBackend.api.v1.cachingObjects.MostDominantRunesCache;
import net.cerberus.challengeBackend.api.v1.cachingObjects.RunesAnalyzedCached;
import net.cerberus.challengeBackend.io.logs.LogLevel;
import net.cerberus.challengeBackend.io.logs.LogReason;
import net.cerberus.challengeBackend.io.logs.Logger;

import java.util.concurrent.TimeUnit;

public class FactCachingService extends CachingService {

    public RunesAnalyzedCached getAnalyzedRunesCache() {
        if (this.needsToBeRefreshed(RunesAnalyzedCached.class)) {
            Logger.logMessage("Re caching the amount of analyzed runes.", LogLevel.INFO, LogReason.CACHE);
            super.cache.put(RunesAnalyzedCached.class, new RunesAnalyzedCached(Main.getDatabaseManager().getAnalyzedRunes()));
        }
        return (RunesAnalyzedCached) super.cache.get(RunesAnalyzedCached.class);
    }

    public MostDominantRuneCache getMostDominantRuneCache() {
        if (this.needsToBeRefreshed(MostDominantRuneCache.class)) {
            Logger.logMessage("Re caching the most dominant rune.", LogLevel.INFO, LogReason.CACHE);
            super.cache.put(MostDominantRuneCache.class, Main.getDatabaseManager().getMostDominantRune());
        }
        return (MostDominantRuneCache) super.cache.get(MostDominantRuneCache.class);
    }

    public LeastPickedRuneCache getLeastPickedRune() {
        if (this.needsToBeRefreshed(LeastPickedRuneCache.class)) {
            Logger.logMessage("Re caching the least picked rune.", LogLevel.INFO, LogReason.CACHE);
            super.cache.put(LeastPickedRuneCache.class, Main.getDatabaseManager().getLeastPickedRune());
        }
        return (LeastPickedRuneCache) super.cache.get(LeastPickedRuneCache.class);
    }

    public MostDominantRunesCache getMostDominantRunes() {
        if (this.needsToBeRefreshed(MostDominantRunesCache.class)) {
            Logger.logMessage("Re caching the most dominant runes.", LogLevel.INFO, LogReason.CACHE);
            super.cache.put(MostDominantRunesCache.class, Main.getDatabaseManager().getMostDominantRunesPerTimeIds());
        }
        return (MostDominantRunesCache) super.cache.get(MostDominantRunesCache.class);
    }

    private boolean needsToBeRefreshed(Class targetClass) {
        if (!super.cache.containsKey(targetClass)) {
            return true;
        }
        TimeUnit timeUnit = super.cache.get(targetClass).getTimeUnit();
        long cachingTime = super.cache.get(targetClass).getCachingTime();
        long timeStamp = super.cache.get(targetClass).getTimeStamp();
        return System.nanoTime() > timeStamp + timeUnit.toNanos(cachingTime);
    }

}
