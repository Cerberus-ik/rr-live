package net.cerberus.gg;

import net.cerberus.gg.db.DatabaseManager;
import net.cerberus.gg.io.logs.LogLevel;
import net.cerberus.gg.io.logs.LogReason;
import net.cerberus.gg.io.logs.Logger;
import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.match.MatchListMatch;
import net.cerberus.riotApi.exception.InvalidApiKeyException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("Duplicates")
public class Main {

    private static RiotApi riotApi;
    private static int coolDown;

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader fileReader;
        try {
            fileReader = new BufferedReader(new FileReader(new File("resources/config.json")));
            fileReader.lines().forEach(stringBuilder::append);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject configFileObject = new JSONObject(stringBuilder.toString());
        coolDown = configFileObject.getJSONObject("gg-config").getInt("coolDownBetweenCalls");
        JSONObject databaseConfig = configFileObject.getJSONObject("gg-config").getJSONObject("database");
        DatabaseManager databaseManager = new DatabaseManager(
                databaseConfig.getString("host"),
                databaseConfig.getString("db"),
                databaseConfig.getString("db_user"),
                databaseConfig.getString("db_pw"));
        databaseManager.connect();
        Logger.logMessage("Connected to database.", LogLevel.INFO, LogReason.DB);
        try {
            riotApi = new RiotApi(configFileObject.getString("key"), 5000);
        } catch (InvalidApiKeyException e) {
            e.printStackTrace();
        }
        stringBuilder = new StringBuilder();
        try {
            Logger.logMessage("Reading summoners...", LogLevel.INFO, LogReason.GG);
            fileReader = new BufferedReader(new FileReader(new File("resources/sg-summoners.json")));
            fileReader.lines().forEach(stringBuilder::append);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonSummoners = new JSONObject(stringBuilder.toString()).getJSONArray("data");
        Logger.logMessage(jsonSummoners.length() + " summoners loaded.", LogLevel.INFO, LogReason.GG);
        gatherData(configFileObject.getJSONObject("gg-config").getInt("summonerBulk"), jsonSummoners, databaseManager);
        databaseManager.closeConnection();
    }

    private static void gatherData(int threadSize, JSONArray jsonSummoners, DatabaseManager databaseManager) {
        while (true) {
            if (jsonSummoners.length() == 0) {
                break;
            }
            if (threadSize > jsonSummoners.length()) {
                threadSize = jsonSummoners.length();
            }
            ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
            HashMap<Summoner, Region> summoners = new HashMap<>();
            for (int i = 0; i < threadSize; i++) {
                JSONObject summonerObject = jsonSummoners.getJSONObject(i);
                executorService.submit(new SummonerRunnable(
                        summonerObject.getLong("summonerId"),
                        riotApi,
                        Region.parseRegionByPlatformId(summonerObject.getString("platformId")),
                        summoners));
                jsonSummoners.remove(i);
            }
            try {
                executorService.shutdown();
                executorService.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Logger.logMessage("A summoner thread is frozen.", LogLevel.ERROR, LogReason.RIOT_API);
                e.printStackTrace();
            }
            Logger.logMessage("Loaded " + summoners.size() + " summoners.", LogLevel.INFO, LogReason.GG);
            List<MatchListMatch> matchListMatches = new ArrayList<>();
            executorService = Executors.newFixedThreadPool(threadSize);
            for (Summoner summoner : summoners.keySet()) {
                executorService.submit(new MatchListRunnable(summoner, riotApi, summoners.get(summoner), matchListMatches, databaseManager));
            }
            try {
                executorService.shutdown();
                executorService.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Logger.logMessage("A match list match thread is frozen.", LogLevel.ERROR, LogReason.RIOT_API);
                e.printStackTrace();
            }
            Logger.logMessage("Loading " + matchListMatches.size() + " new matches.", LogLevel.INFO, LogReason.GG);
            executorService = Executors.newFixedThreadPool(threadSize);
            for (MatchListMatch matchListMatch : matchListMatches) {
                executorService.submit(new MatchRunnable(matchListMatch, riotApi, databaseManager));
            }
            try {
                executorService.shutdown();
                executorService.awaitTermination(50, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Logger.logMessage("A match thread is frozen.", LogLevel.ERROR, LogReason.RIOT_API);
                e.printStackTrace();
            }
            Logger.logMessage("Finished bulk.", LogLevel.INFO, LogReason.GG);
        }
    }

//    @Deprecated
//    private static void gatherData(JSONArray jsonSummoners, DatabaseManager databaseManager){
//        for (int i = 0; i < jsonSummoners.length(); i++) {
//            sleep();
//            JSONObject jsonSummoner = jsonSummoners.getJSONObject(i);
//            Region region = Region.parseRegionByPlatformId(jsonSummoner.getString("platformId"));
//            try {
//                Summoner summoner = riotApi.summonerApi.getSummonerById(jsonSummoner.getLong("summonerId"), region);
//                MatchList matchList = riotApi.matchApi.getMatchListByAccountId(summoner.getAccountId(), region, Queue.TEAM_BUILDER_RANKED_SOLO, null, null, null, null, Season.SEASON2017, null);
//                List<MatchListMatch> summonerMatches = matchList.getMatches()
//                        .stream()
//                        .filter(matchListMatch -> !databaseManager.gameAlreadyCached(matchListMatch.getGameId(), region) && matchListMatch.getTimestamp() > 1511568000000L)
//                        .collect(Collectors.toList());
//                ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(summonerMatches.size());
//                summonerMatches.forEach(summonerMatch -> scheduledExecutorService.execute(new GameRunnable(summoner, riotApi, summonerMatch, databaseManager)));
//            } catch (RiotApiRequestException e) {
//                e.printStackTrace();
//                System.out.println(e.getResponseCode());
//                System.out.println(jsonSummoner.toString());
//            }
//            Logger.logMessage("Summoner " + (i + 1) + " from " + jsonSummoners.length(), LogLevel.INFO, LogReason.GG);
//        }
//    }

    private static void sleep() {
        if (coolDown <= 0) {
            return;
        }
        try {
            Thread.sleep(coolDown);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

