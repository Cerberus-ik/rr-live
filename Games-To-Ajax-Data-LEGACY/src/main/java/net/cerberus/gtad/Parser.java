package net.cerberus.gtad;

import net.cerberus.gtad.common.Role;
import net.cerberus.gtad.common.TimeStep;
import net.cerberus.gtad.io.logger.LogLevel;
import net.cerberus.gtad.io.logger.LogReason;
import net.cerberus.gtad.io.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@SuppressWarnings("Duplicates")
class Parser {

    private File directory;
    private List<JSONObject> rawData;
    private long time;
    private long firstGameTimeStamp;

    Parser(File file, long time) {
        this.directory = file;
        this.time = time;
    }

    void read() {
        try {
            Logger.logMessage("Reading files...", LogLevel.INFO, LogReason.PARSER);
            this.rawData = new ArrayList<>();
            Files.list(Paths.get(this.directory.toURI())).forEach(gameFile -> {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(gameFile.toFile()));
                    StringBuilder stringBuilder = new StringBuilder();
                    bufferedReader.lines().forEach(stringBuilder::append);
                    bufferedReader.close();
                    rawData.add(new JSONObject(stringBuilder.toString()));
                } catch (IOException | JSONException e) {
                    System.out.println(gameFile);
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<TimeStep> start() {
        Logger.logMessage("Starting to parse the games.", LogLevel.INFO, LogReason.PARSER);
        this.firstGameTimeStamp = Long.MAX_VALUE;
        for (JSONObject rawDataEntry : this.rawData) {
            if (this.firstGameTimeStamp > rawDataEntry.getJSONObject("game").getLong("timestamp")) {
                this.firstGameTimeStamp = rawDataEntry.getJSONObject("game").getLong("timestamp");
            }
        }
        Date date = new Date(this.firstGameTimeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        System.out.println("First game: " + simpleDateFormat.format(date));
        List<TimeStep> timeSteps = new ArrayList<>();
        for (JSONObject rawGame : this.rawData) {
            long timestamp = rawGame.getJSONObject("game").getLong("timestamp");
            timestamp = timestamp - this.firstGameTimeStamp;
            int id = (int) Math.floorDiv(timestamp, this.time);
            if (timeSteps.stream().noneMatch(timeStep -> timeStep.id == id)) {
                TimeStep timeStep = new TimeStep();
                timeStep.id = id;

                timeStep.timeStepSize = this.time;
                TreeMap<Integer, Integer> top = new TreeMap<>();
                TreeMap<Integer, Integer> jung = new TreeMap<>();
                TreeMap<Integer, Integer> mid = new TreeMap<>();
                TreeMap<Integer, Integer> adc = new TreeMap<>();
                TreeMap<Integer, Integer> sup = new TreeMap<>();
                timeStep.usageOfRunes = new TreeMap<>();
                timeStep.usageOfRunes.put(Role.TOP, top);
                timeStep.usageOfRunes.put(Role.JUNGLE, jung);
                timeStep.usageOfRunes.put(Role.MID, mid);
                timeStep.usageOfRunes.put(Role.ADC, adc);
                timeStep.usageOfRunes.put(Role.SUPPORT, sup);
                timeSteps.add(timeStep);
            }
            TimeStep timeStep = timeSteps.stream().filter(timeStepStream -> timeStepStream.id == id).findFirst().orElseThrow(NullPointerException::new);
            timeSteps.remove(timeStep);
            timeStep = this.addRunesToStep(timeStep, rawGame);
            timeSteps.add(timeStep);
        }
        return timeSteps;
    }

    private TimeStep addRunesToStep(TimeStep timeStep, JSONObject game) {
        for (int i = 1; i < 11; i++) {
            String lane = game.getString(i + "-lane");
            int runeId = game.getInt(i + "-perk0");
            TreeMap<Integer, Integer> roleRunes = null;
            switch (lane) {
                case "JUNGLE":
                    roleRunes = timeStep.usageOfRunes.get(Role.JUNGLE);
                    break;
                case "MIDDLE":
                    roleRunes = timeStep.usageOfRunes.get(Role.MID);
                    break;
                case "TOP":
                    roleRunes = timeStep.usageOfRunes.get(Role.TOP);
                    break;
                case "BOTTOM": {
                    String role = game.getString(i + "-role");
                    switch (role) {
                        case "DUO_CARRY":
                            roleRunes = timeStep.usageOfRunes.get(Role.ADC);
                            break;
                        case "SOLO":
                            roleRunes = timeStep.usageOfRunes.get(Role.ADC);
                            break;
                        case "DUO_SUPPORT":
                            roleRunes = timeStep.usageOfRunes.get(Role.SUPPORT);
                            break;
                        default:
                            roleRunes = timeStep.usageOfRunes.get(Role.SUPPORT);
                            break;
                    }
                    break;
                }
            }
            if (roleRunes.containsKey(runeId)) {
                int runeCount = roleRunes.get(runeId);
                roleRunes.put(runeId, runeCount + 1);
            } else {
                roleRunes.put(runeId, 1);
            }
            timeStep.usageOfRunes.remove(Role.JUNGLE);
            timeStep.usageOfRunes.put(Role.JUNGLE, roleRunes);
        }
        return timeStep;
    }

    public long getFirstGameTimeStamp() {
        return firstGameTimeStamp;
    }
}
