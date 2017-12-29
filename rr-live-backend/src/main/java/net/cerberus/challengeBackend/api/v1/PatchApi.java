package net.cerberus.challengeBackend.api.v1;

import net.cerberus.challengeBackend.api.API;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.halt;

public class PatchApi extends API {

    public PatchApi() {
        this.getPatchByTimestamp();
        this.getPatches();
    }

    private void getPatchByTimestamp() {
        get("/api/v1/patch/getPatchByTimestamp", ((request, response) -> {
            if (!isConnectionAllowed(request)) {
                return halt(401, "Unauthorized");
            }
            String timeStampString = request.queryParams("timestamp");
            if (timeStampString == null) {
                return halt(400, "Bad Request");
            }
            response.type("application/json");
            JSONArray versions = this.getVersionObject();
            long timestamp = Long.valueOf(timeStampString) / 1000;
            List<JSONObject> jsonPatches = new ArrayList<>();
            for (int i = 0; i < versions.length(); i++) {
                jsonPatches.add(versions.getJSONObject(i));
            }
            JSONObject resultObject = new JSONObject();
            resultObject.put("patch", jsonPatches
                    .stream()
                    .map(JSONObject.class::cast)
                    .filter(jsonObject -> jsonObject.getJSONObject("start").has("EUW") && jsonObject.getJSONObject("start").getLong("EUW") > timestamp)
                    .min(Comparator.comparingLong(time -> time.getJSONObject("start").getLong("EUW")))
                    .orElse(new JSONObject()).getString("name"));
            return resultObject.toString();
        }));
    }

    private void getPatches() {
        get("/api/v1/patch/getPatches", ((request, response) -> {
            if (!isConnectionAllowed(request)) {
                return halt(401, "Unauthorized");
            }
            String timeStampString = request.queryParams("since");
            if (timeStampString == null) {
                return halt(400, "Bad Request");
            }
            response.type("application/json");
            JSONArray versions = this.getVersionObject();
            long timestamp = Long.valueOf(timeStampString) / 1000 - 1000;
            List<JSONObject> jsonPatches = new ArrayList<>();
            for (int i = 0; i < versions.length(); i++) {
                jsonPatches.add(versions.getJSONObject(i));
            }
            JSONArray resultArray = new JSONArray();
            jsonPatches.stream()
                    .filter(streamObject -> {
                        if (streamObject.getJSONObject("start").has("EUW") && streamObject.getJSONObject("start").getLong("EUW") >= timestamp) {
                            return true;
                        } else if (jsonPatches.indexOf(streamObject) + 1 < jsonPatches.size()) {
                            JSONObject nextObject = jsonPatches.get(jsonPatches.indexOf(streamObject) + 1);
                            return nextObject.getJSONObject("start").has("EUW") && nextObject.getJSONObject("start").getLong("EUW") >= timestamp;
                        }
                        return false;
                    })
                    .forEach(streamPatchObject -> {
                        JSONObject resultPatchObject = new JSONObject();
                        resultPatchObject.put("start", streamPatchObject.getJSONObject("start").getLong("EUW") * 1000);
                        resultPatchObject.put("patch", streamPatchObject.getString("name"));
                        resultArray.put(resultPatchObject);
                    });
            if (resultArray.length() == 0) {
                JSONObject resultPatchObject = jsonPatches.get(jsonPatches.size() - 1);
                resultPatchObject.put("start", resultPatchObject.getJSONObject("start").getLong("EUW") * 1000);
                resultPatchObject.put("patch", resultPatchObject.getString("name"));
                resultArray.put(resultPatchObject);
            }
            return resultArray.toString();
        }));
    }

    private JSONArray getVersionObject() {
        try {
            URL url = new URL(System.getenv("VERSION_URL"));
            URLConnection connection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            bufferedReader.lines().forEach(stringBuilder::append);
            return new JSONArray(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }
}
