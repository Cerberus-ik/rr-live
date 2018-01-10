package net.cerberus.gtad;

import net.cerberus.gtad.common.Role;
import net.cerberus.gtad.common.TimeStep;
import net.cerberus.gtad.db.DatabaseManager;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    private static long startingTime;

    public static void main(String[] args) {
        Logger.logMessage("Starting the GTAD converter.", LogLevel.INFO, LogReason.PARSER);
        startingTime = System.nanoTime();
        if (args.length < 4) {
            System.out.println("Define the path (--path) to the games directory and the time parts (--time)");
            return;
        }
        List<String> argsList = Arrays.asList(args);
        int timeId = argsList.indexOf("--time");
        int pathId = argsList.indexOf("--path");
        String path = argsList.get(pathId + 1);
        long time = Long.parseLong(argsList.get(timeId + 1));

        if (path == null || time == 0) {
            System.out.println("Define the path (--path) to the games directory and the time parts (--time)");
            return;
        }
        File directory = parsePath(path);
        if (directory == null) {
            return;
        }
        Parser parser = new Parser(directory, time);
        parser.read();
        List<TimeStep> steps = parser.start();
        pushSteps(steps, parser.getFirstGameTimeStamp());

        Logger.logMessage("Finished pushing ajax data.", LogLevel.INFO, LogReason.CACHE);
        Logger.logMessage("The process took: " + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startingTime) + " seconds.", LogLevel.INFO, LogReason.CACHE);
    }

    private static void pushSteps(List<TimeStep> steps, long firstGame) {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.connect();
        databaseManager.truncateDatabase();
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
            databaseManager.updateId(step.id, stepObject.toString());
        });
        databaseManager.closeConnection();
    }

//    private static void pushSteps(List<TimeStep> steps, long firstGame) {
//        File file = new File("Games-To-Ajax-Data/output/data.json");
//        if (!file.exists()) {
//            try {
//                if (!file.createNewFile()) {
//                    System.out.println("Error while creating the output file.");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        JSONObject outputObject = new JSONObject();
//        outputObject.put("stepSize", steps.get(0).timeStepSize);
//        outputObject.put("firstGame", firstGame);
//        steps.forEach(step -> {
//            JSONObject stepObject = new JSONObject();
//            stepObject.put("id", step.id);
//            for (Role role : step.usageOfRunes.keySet()) {
//                JSONArray runesArray = new JSONArray();
//                TreeMap<Integer, Integer> runesPerRole = step.usageOfRunes.get(role);
//                for (Integer runeId : runesPerRole.keySet()) {
//                    JSONObject runeObject = new JSONObject();
//                    runeObject.put(String.valueOf(runeId), runesPerRole.get(runeId));
//                    runesArray.put(runeObject);
//                }
//                stepObject.put(role.name(), runesArray);
//            }
//            outputObject.put(String.valueOf(step.id), stepObject);
//        });
//        try {
//            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
//            bufferedWriter.write(outputObject.toString());
//            bufferedWriter.flush();
//            bufferedWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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

    private static File parsePath(String path) {
        if (path == null) {
            System.out.println("Invalid path");
            return null;
        }
        File file = new File(path);
        if (!file.isDirectory()) {
            System.out.println("The specified path is not a valid directory.");
            return null;
        }
        try {
            Logger.logMessage("Counting games...", LogLevel.INFO, LogReason.FILE_READER);
            long fileCount = Files.list(Paths.get(file.toURI())).count();
            Logger.logMessage(fileCount + " files found.", LogLevel.INFO, LogReason.FILE_READER);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
