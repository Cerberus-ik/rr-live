package net.cerberus.gg;

import net.cerberus.gg.io.logs.LogLevel;
import net.cerberus.gg.io.logs.LogReason;
import net.cerberus.gg.io.logs.Logger;
import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Queue;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.constants.Season;
import net.cerberus.riotApi.common.match.MatchList;
import net.cerberus.riotApi.common.match.MatchListMatch;
import net.cerberus.riotApi.exception.InvalidApiKeyException;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

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
        for (int i = 0; i < jsonSummoners.length(); i++) {
            sleep();
            JSONObject jsonSummoner = jsonSummoners.getJSONObject(i);
            Region region = Region.parseRegionByPlatformId(jsonSummoner.getString("platformId"));
            try {
                Summoner summoner = riotApi.summonerApi.getSummonerById(jsonSummoner.getLong("summonerId"), region);
                MatchList matchList = riotApi.matchApi.getMatchListByAccountId(summoner.getAccountId(), region, Queue.TEAM_BUILDER_RANKED_SOLO, 0, 0, null, null, Season.SEASON2017, null);
                List<MatchListMatch> summonerMatches = matchList.getMatches()
                        .stream()
                        .filter(matchListMatch -> !gameAlreadyCached(matchListMatch.getGameId()) && matchListMatch.getTimestamp() > 1511568000000L)
                        .collect(Collectors.toList());
                ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(summonerMatches.size());
                summonerMatches.forEach(summonerMatch -> scheduledExecutorService.execute(new GameRunnable(summoner, riotApi, summonerMatch, region)));
            } catch (RiotApiRequestException e) {
                e.printStackTrace();
            }
            Logger.logMessage("Summoner " + (i + 1) + " from " + jsonSummoners.length(), LogLevel.INFO, LogReason.GG);
        }
    }

    private static boolean gameAlreadyCached(long matchId) {
        File file = new File("Z:/libs/resources/games/" + matchId + ".json");
        /* Use this for testing purposes. */
        //File file = new File("resources/games/" + match.getGameId() + ".json");
        return file.exists();
    }

    private static boolean isVersionAllowed(String version) {
        String[] parts = version.split(".");
        return Integer.parseInt(parts[0]) > 7 || Integer.parseInt(parts[1]) > 22;
    }

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

