package net.cerberus.challengeBackend.api.v1;

import net.cerberus.challengeBackend.Main;
import net.cerberus.challengeBackend.api.API;
import net.cerberus.challengeBackend.common.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.halt;

public class RunesApi extends API {

    public RunesApi() {
        this.getRunes();
        this.getLatestStepId();
    }

    private void getRunes() {
        get("/api/v1/runes/getRunes", (request, response) -> {
            response.type("application/json");
            if (!super.isConnectionAllowed(request)) {
                return halt(401, "Unauthorized");
            }
            String id = request.queryParamOrDefault("id", null);
            String amount = request.queryParamOrDefault("amount", "10");
            if (id == null) {
                return halt(400, "Bad Request");
            }
            int integerId;
            int amountOfIds;
            try {
                integerId = Integer.parseInt(id);
                amountOfIds = Integer.parseInt(amount);
            } catch (Exception e) {
                return halt(400, "Bad Request");
            }
            List<String> results = Main.getDatabaseManager().getIdObject(integerId, amountOfIds);
            if (results.size() == 0) {
                return halt(404, "Id not found");
            }

            JSONArray resultsArray = new JSONArray();
            results.forEach(result -> resultsArray.put(new JSONObject(result)));
            JSONArray runeOrder = resultsArray.getJSONObject(0).getJSONArray("runeOrder");
            JSONObject responseObject = new JSONObject();
            responseObject.put("stepSize", resultsArray.getJSONObject(0).getLong("stepSize"));
            responseObject.put("firstGame", resultsArray.getJSONObject(0).getLong("firstGame"));
            responseObject.put("runeOrder", runeOrder);
            for (Role role : Role.values()) {
                JSONArray roleArray = new JSONArray();
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONArray stepRoleArray = new JSONArray();
                    JSONObject originalIdObject = resultsArray.getJSONObject(i);
                    for (int r = 0; r < runeOrder.length(); r++) {
                        if (originalIdObject.getJSONArray(role.name()).length() - 1 < r) {
                            stepRoleArray.put(0);
                        } else {
                            JSONObject runeIdRoleObject = originalIdObject.getJSONArray(role.name()).getJSONObject(r);
                            stepRoleArray.put(runeIdRoleObject.getInt(runeIdRoleObject.keys().next()));
                        }
                    }
                    roleArray.put(stepRoleArray);
                }
                responseObject.put(role.name(), roleArray);
            }
            return responseObject.toString();
        });
    }

    private void getLatestStepId() {
        get("/api/v1/runes/getLatestStepId", (request, response) -> {
            response.type("application/json");
            if (!isConnectionAllowed(request)) {
                return halt(401, "Unauthorized");
            }
            int id = Main.getDatabaseManager().getLatestStepId();
            if (id == -1) {
                return halt(404, "Data not found");
            }
            response.type("application/json");
            JSONObject result = new JSONObject();
            result.put("latestStepId", id);
            return result.toString();
        });
    }
}
