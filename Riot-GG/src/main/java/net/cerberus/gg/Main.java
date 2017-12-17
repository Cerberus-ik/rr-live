package net.cerberus.gg;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Queue;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.constants.ResponseCode;
import net.cerberus.riotApi.common.constants.Season;
import net.cerberus.riotApi.common.match.Match;
import net.cerberus.riotApi.common.match.MatchList;
import net.cerberus.riotApi.exception.InvalidApiKeyException;
import net.cerberus.riotApi.exception.RateLimitException;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class Main {

    private static RiotApi riotApi;

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
        JSONObject keyObject = new JSONObject(stringBuilder.toString());
        try {
            riotApi = new RiotApi(keyObject.getString("key"), 5000);
        } catch (InvalidApiKeyException e) {
            e.printStackTrace();
        }
        stringBuilder = new StringBuilder();
        try {
            System.out.println("Reading summoners...");
            fileReader = new BufferedReader(new FileReader(new File("resources/summoners.json")));
            fileReader.lines().forEach(stringBuilder::append);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonSummoners = new JSONObject(stringBuilder.toString()).getJSONArray("data");
        System.out.println(jsonSummoners.length() + " summoners loaded.");
        for (int i = 0; i < jsonSummoners.length(); i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject jsonSummoner = jsonSummoners.getJSONObject(i);
            Region region = Region.parseRegionByPlatformId(jsonSummoner.getString("platformId"));
            try {
                Summoner summoner = riotApi.summonerApi.getSummonerById(jsonSummoner.getLong("summonerId"), region);
                MatchList matchList = riotApi.matchApi.getMatchListByAccountId(summoner.getAccountId(), region, Queue.TEAM_BUILDER_RANKED_SOLO, 0, 0, null, null, Season.SEASON2017, null);
                matchList.getMatches().stream().filter(matchListMatch -> matchListMatch.getTimestamp() > 1511568000000L).forEach(matchListMatch -> {
                    if (!gameAlreadyCached(matchListMatch.getGameId())) {
                        try {
                            Match match = riotApi.matchApi.getMatchById(matchListMatch.getGameId(), region);
                            System.out.println("Parsing match: " + match.getGameId() + " from " + summoner.getName());
                            JSONObject jsonGame = new JSONObject();
                            JSONObject dataObject = new JSONObject();
                            dataObject.put("timestamp", match.getGameCreation());
                            dataObject.put("patch", match.getGameVersion());
                            dataObject.put("region", match.getPlatformId());
                            jsonGame.put("game", dataObject);
                            match.getParticipants().forEach(participant -> {
                                jsonGame.put(participant.getParticipantId() + "-perk0", participant.getStats().getPerk0());
                                jsonGame.put(participant.getParticipantId() + "-perk1", participant.getStats().getPerk1());
                                jsonGame.put(participant.getParticipantId() + "-perk2", participant.getStats().getPerk2());
                                jsonGame.put(participant.getParticipantId() + "-perk3", participant.getStats().getPerk3());
                                jsonGame.put(participant.getParticipantId() + "-perk4", participant.getStats().getPerk4());
                                jsonGame.put(participant.getParticipantId() + "-perk5", participant.getStats().getPerk5());
                                jsonGame.put(participant.getParticipantId() + "-lane", participant.getTimeline().getLane());
                                jsonGame.put(participant.getParticipantId() + "-role", participant.getTimeline().getRole());
                            });
                            File file = new File("Z:/libs/resources/games/" + match.getGameId() + ".json");
                            /* Use this for testing purposes. */
                            //File file = new File("resources/games/" + match.getGameId() + ".json");
                            if (!file.createNewFile()) {
                                System.out.println("Error while creating file " + file.getAbsolutePath());
                            }
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                            bufferedWriter.write(jsonGame.toString());
                            bufferedWriter.flush();
                            bufferedWriter.close();
                            try {
                                Thread.sleep(1100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (RiotApiRequestException e) {
                            if (e.getResponseCode().equals(ResponseCode.RATE_LIMIT_EXCEEDED)) {
                                RateLimitException rateLimitException = (RateLimitException) e;
                                try {
                                    Thread.sleep(rateLimitException.getRetryAfter() * 1000 + 100);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (RiotApiRequestException e) {
                e.printStackTrace();
            }
            System.out.println("Summoner " + (i + 1) + " from " + jsonSummoners.length());
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
}

