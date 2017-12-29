package net.cerberus.challengeBackend.api;

import java.util.concurrent.TimeUnit;

public interface CachingObject {

    TimeUnit getTimeUnit();

    long getCachingTime();

    long getTimeStamp();
}
