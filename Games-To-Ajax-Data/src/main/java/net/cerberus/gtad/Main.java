package net.cerberus.gtad;

import net.cerberus.gtad.common.Role;
import net.cerberus.gtad.common.TimeStep;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
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
        saveSteps(steps, parser.getFirstGameTimeStamp());
    }

    private static void saveSteps(List<TimeStep> steps, long firstGame) {
        File file = new File("Games-To-Ajax-Data/output/data.json");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    System.out.println("Error while creating the output file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONObject outputObject = new JSONObject();
        outputObject.put("stepSize", steps.get(0).timeStepSize);
        outputObject.put("firstGame", firstGame);
        steps.forEach(step -> {
            JSONObject stepObject = new JSONObject();
            stepObject.put("id", step.id);
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
            outputObject.put(String.valueOf(step.id), stepObject);
        });
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(outputObject.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            long fileCount = Files.list(Paths.get(file.toURI())).count();
            System.out.println(fileCount + " files found.");
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
