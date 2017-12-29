package net.cerberus.challengeBackend.api.v1;

import net.cerberus.challengeBackend.api.API;
import net.cerberus.challengeBackend.api.v1.cachingObjects.LeastPickedRuneCache;
import net.cerberus.challengeBackend.api.v1.cachingObjects.MostDominantRuneCache;
import net.cerberus.challengeBackend.api.v1.cachingObjects.MostDominantRunesCache;
import net.cerberus.challengeBackend.api.v1.cachingServices.FactCachingService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static spark.Spark.get;
import static spark.Spark.halt;

public class FactApi extends API {

    private FactCachingService cachingService;

    public FactApi() {
        this.cachingService = new FactCachingService();
        this.getTotalAnalyzedRunes();
        this.getMostDominantRune();
        this.getLeastPickedRoleRune();
        this.getAmountRunesGotPicked();
    }

    private void getTotalAnalyzedRunes() {
        get("/api/v1/fact/getAnalyzedRunes", (request, response) -> {
            if (!isConnectionAllowed(request)) {
                return halt(401, "Unauthorized");
            }
            response.type("application/json");
            JSONObject resultObject = new JSONObject();
            resultObject.put("totalAnalyzedRunes", this.cachingService.getAnalyzedRunesCache().getAnalyzedRunes());
            return resultObject.toString();
        });
    }

    private void getMostDominantRune() {
        get("/api/v1/fact/getMostDominantRune", ((request, response) -> {
            if (!isConnectionAllowed(request)) {
                return halt(401, "Unauthorized");
            }
            response.type("application/json");
            JSONObject resultObject = new JSONObject();
            MostDominantRuneCache mostDominantRuneCache = this.cachingService.getMostDominantRuneCache();
            resultObject.put("rune", mostDominantRuneCache.getRuneId());
            resultObject.put("role", mostDominantRuneCache.getRole());
            resultObject.put("timeId", mostDominantRuneCache.getTimeId());
            resultObject.put("timesPicked", mostDominantRuneCache.getTimesPicked());
            resultObject.put("totalRunesPicked", mostDominantRuneCache.getTotalRunesPicked());
            return resultObject.toString();
        }));
    }

    //TODO percentages?
    private void getLeastPickedRoleRune() {
        get("/api/v1/fact/getLeastPickedRoleRune", ((request, response) -> {
            if (!isConnectionAllowed(request)) {
                return halt(401, "Unauthorized");
            }
            response.type("application/json");
            JSONObject resultObject = new JSONObject();
            LeastPickedRuneCache leastPickedRuneCache = this.cachingService.getLeastPickedRune();
            resultObject.put("runeId", leastPickedRuneCache.getRuneId());
            resultObject.put("timesPicked", leastPickedRuneCache.getTimesPicked());
            return resultObject.toString();
        }));
    }

    private void getAmountRunesGotPicked() {
        get("/api/v1/fact/getAmountRunesGotPicked", ((request, response) -> {
            if (!isConnectionAllowed(request)) {
                return halt(401, "Unauthorized");
            }
            response.type("application/json");
            JSONArray resultArray = new JSONArray();
            MostDominantRunesCache mostDominantRunesCache = this.cachingService.getMostDominantRunes();
            HashMap<Integer, Long> timesRunePicked = new HashMap<>();
            mostDominantRunesCache.getRunes()
                    .forEach(mostDominantRunesPerTimeId -> mostDominantRunesPerTimeId.roleRunes().values()
                            .forEach(roleRune -> timesRunePicked.put(roleRune.getRuneId(), timesRunePicked.getOrDefault(roleRune.getRuneId(), 0L) + roleRune.getTimesPicked())));
            for (Integer runeId : timesRunePicked.keySet()) {
                JSONObject runeObject = new JSONObject();
                runeObject.put("runeId", runeId);
                runeObject.put("timesPicked", timesRunePicked.get(runeId));
                resultArray.put(runeObject);
            }
            return resultArray.toString();
        }));
    }
}
