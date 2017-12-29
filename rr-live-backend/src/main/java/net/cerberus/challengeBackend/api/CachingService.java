package net.cerberus.challengeBackend.api;

import java.util.HashMap;

public class CachingService {

    public HashMap<Class, CachingObject> cache;

    public CachingService() {
        this.cache = new HashMap<>();
    }
}
