package net.cerberus.gg;

import net.cerberus.gg.db.DatabaseManager;
import net.cerberus.gg.io.logs.LogLevel;
import net.cerberus.gg.io.logs.LogReason;
import net.cerberus.gg.io.logs.Logger;
import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Queue;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.constants.Season;
import net.cerberus.riotApi.common.match.MatchListMatch;
import net.cerberus.riotApi.exception.InvalidApiKeyException;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    private static RiotApi riotApi;
    private static int coolDown;
    private static List<Long> games;

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        games = new ArrayList<>();
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
                databaseConfig.getString("db_user"),
                databaseConfig.getString("db_pw"),
                databaseConfig.getString("host"),
                databaseConfig.getString("db")
        );
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
        gatherData(jsonSummoners, databaseManager);
    }

    private static void gatherData(JSONArray jsonSummoners, DatabaseManager databaseManager) {
        Random random = new Random();
        while (true) {
            if (jsonSummoners.length() == 0) {
                break;
            }
            int i = random.nextInt(jsonSummoners.length() - 1);
            JSONObject summonerObject = jsonSummoners.getJSONObject(i);
            Summoner summoner = getSummoner(summonerObject.getLong("summonerId"), Region.parseRegionByPlatformId(summonerObject.getString("platformId")));
            if (summoner == null) {
                continue;
            }
            Logger.logMessage("Getting summoner: " + summoner.getName() + " " + jsonSummoners.length() + " left.", LogLevel.INFO, LogReason.GG);
            List<MatchListMatch> matchListMatches = getMatchList(summoner, Region.parseRegionByPlatformId(summonerObject.getString("platformId")), databaseManager);
            if (matchListMatches.size() == 0) {
                jsonSummoners.remove(0);
                continue;
            }
            Logger.logMessage("Loading " + matchListMatches.size() + " new matches.", LogLevel.INFO, LogReason.GG);
            ExecutorService executorService = Executors.newFixedThreadPool(matchListMatches.size());
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
            jsonSummoners.remove(i);
        }
    }

    private static List<MatchListMatch> getMatchList(Summoner summoner, Region region, DatabaseManager databaseManager) {
        try {
            return riotApi.matchApi.getMatchListByAccountId(
                    summoner.getAccountId(),
                    region,
                    Queue.TEAM_BUILDER_RANKED_SOLO,
                    null,
                    null,
                    null,
                    null, Season.SEASON2017,
                    null)
                    .getMatches()
                    .stream()
                    .filter(
                            matchListMatch ->
                                    matchListMatch.getTimestamp() > 1511568000000L &&
                                            !games.contains(matchListMatch.getGameId()) &&
                                            !databaseManager.isGameCached(matchListMatch.getGameId(), region))
                    .collect(Collectors.toList());
        } catch (RiotApiRequestException e) {
            System.out.println(e.getResponseCode());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Returns a summoner.
     *
     * @param summonerId target summoner id.
     * @param region     target summoner region.
     * @return the summoner and null if the user does not exist.
     */
    private static Summoner getSummoner(long summonerId, Region region) {
        try {
            return riotApi.summonerApi.getSummonerById(summonerId, region);
        } catch (RiotApiRequestException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method was used to slow down the game gathering process.
     * With a much more powerful api key this is no longer necessary.
     */
    @Deprecated
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

    /**
     * This list keeps track of games that have already been cached this run.
     * It reduces the load on the database slightly.
     *
     * @return all game ids that have been cached.
     */
    public static List<Long> getGames() {
        return games;
    }
}

