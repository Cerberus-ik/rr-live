package net.cerberus.gtad;

import net.cerberus.gtad.common.DatabaseCredentials;
import net.cerberus.gtad.common.Role;
import net.cerberus.gtad.common.TimeStep;
import net.cerberus.gtad.db.TargetDatabaseManager;
import net.cerberus.gtad.io.logger.LogLevel;
import net.cerberus.gtad.io.logger.LogReason;
import net.cerberus.gtad.io.logger.Logger;
import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.exception.InvalidApiKeyException;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Logger.logMessage("Starting the GTAD converter...", LogLevel.INFO, LogReason.GTAD);
        long startingTime = System.nanoTime();
        if (args.length < 2) {
            System.out.println("Define the time parts (--time)");
            return;
        }
        List<String> argsList = Arrays.asList(args);
        int timeId = argsList.indexOf("--time");
        long time = Long.parseLong(argsList.get(timeId + 1));

        if (time == 0) {
            System.out.println("Define the time parts (--time)");
            return;
        }
        JSONObject config = getGTADConfig();
        Parser parser = new Parser(getDatabaseCredentials(config.getJSONObject("sourceDatabase")), time);
        parser.getData();
        List<TimeStep> steps = parser.start();
        pushSteps(steps, parser.getFirstGameTimeStamp(), getDatabaseCredentials(config.getJSONObject("targetDatabase")));

        Logger.logMessage("Finished pushing ajax data.", LogLevel.INFO, LogReason.GTAD);
        Logger.logMessage("The process took: " + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startingTime) + " seconds.", LogLevel.INFO, LogReason.GTAD);
    }

    private static void pushSteps(List<TimeStep> steps, long firstGame, DatabaseCredentials databaseCredentials) {
        TargetDatabaseManager targetDatabaseManager = new TargetDatabaseManager(databaseCredentials);
        targetDatabaseManager.truncateDatabase();
        JSONArray runeIdOrder = getRuneIdsInOrder(dataDragonRunesReforged());
        steps.forEach(step -> {
            JSONObject stepObject = new JSONObject();
            stepObject.put("stepSize", step.timeStepSize);
            stepObject.put("firstGame", firstGame);
            stepObject.put("id", step.id);
            stepObject.put("runeOrder", runeIdOrder);
            for (Role role : step.usageOfRunes.keySet()) {
                JSONArray runesArray = new JSONArray();
                TreeMap<Integer, Integer> runesPerRole = step.usageOfRunes.get(role);
                for (Integer runeId : runesPerRole.keySet()) {
                    JSONObject runeObject = new JSONObject();
                    runeObject.put(String.valueOf(runeId), runesPerRole.get(runeId));
                    runesArray.put(runeObject);
                }
                stepObject.put(role.name(), runesArray);
            }
            targetDatabaseManager.updateId(step.id, stepObject.toString());
        });
    }

    private static JSONArray dataDragonRunesReforged() {
        try {
            RiotApi riotApi = new RiotApi(getAPIKey(), 5000);
            /* TODO replace this with library call when it supports rr */
            String latestVersion = riotApi.dataDragonApi.getVersions().get(0);
            URL url = new URL("http://ddragon.leagueoflegends.com/cdn/" + latestVersion + "/data/en_US/runesReforged.json");
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            bufferedReader.lines().forEach(stringBuilder::append);
            return new JSONArray(stringBuilder.toString());
        } catch (InvalidApiKeyException e) {
            Logger.logMessage("The Api key is invalid.", LogLevel.ERROR, LogReason.RIOT_API);
        } catch (RiotApiRequestException | IOException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private static DatabaseCredentials getDatabaseCredentials(JSONObject databaseObject) {
        return new DatabaseCredentials() {
            @Override
            public int getPort() {
                return 3306;
            }

            @Override
            public String getHost() {
                return databaseObject.getString("host");
            }

            @Override
            public String getDatabase() {
                return databaseObject.getString("db");
            }

            @Override
            public String getUser() {
                return databaseObject.getString("db_user");
            }

            @Override
            public String getPassword() {
                return databaseObject.getString("db_pw");
            }
        };
    }

    private static JSONArray getRuneIdsInOrder(JSONArray runesReforgedData) {
        List<Integer> runIds = new ArrayList<>();
        for (int i = 0; i < runesReforgedData.length(); i++) {
            JSONObject perkStyleObject = runesReforgedData.getJSONObject(i);
            for (int r = 0; r < perkStyleObject.getJSONArray("slots").getJSONObject(0).getJSONArray("runes").length(); r++) {
                runIds.add(perkStyleObject.getJSONArray("slots").getJSONObject(0).getJSONArray("runes").getJSONObject(r).getInt("id"));
            }
        }
        JSONArray resultArray = new JSONArray();
        runIds.stream().sorted(Comparator.naturalOrder()).forEach(resultArray::put);
        return resultArray;
    }

    private static String getAPIKey() {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File("resources/config.json")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        bufferedReader.lines().forEach(stringBuilder::append);
        return new JSONObject(stringBuilder.toString()).getString("key");
    }

    private static JSONObject getGTADConfig() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("resources/config.json")));
            StringBuilder stringBuilder = new StringBuilder();
            bufferedReader.lines().forEach(stringBuilder::append);
            return new JSONObject(stringBuilder.toString()).getJSONObject("gtad-config");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
