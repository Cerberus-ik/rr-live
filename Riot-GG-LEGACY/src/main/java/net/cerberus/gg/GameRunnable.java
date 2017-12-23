package net.cerberus.gg;

import net.cerberus.gg.io.logs.LogLevel;
import net.cerberus.gg.io.logs.LogReason;
import net.cerberus.gg.io.logs.Logger;
import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.constants.ResponseCode;
import net.cerberus.riotApi.common.match.Match;
import net.cerberus.riotApi.common.match.MatchListMatch;
import net.cerberus.riotApi.exception.RateLimitException;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GameRunnable implements Runnable {

    private Summoner summoner;
    private RiotApi riotApi;
    private MatchListMatch matchListMatch;
    private Region region;

    /**
     * This @{@link Runnable} will get a game from the api, parse it and saves it to a file.
     *
     * @param summoner       the summoner the game is from. For logging purposes only.
     * @param riotApi        the api to call.
     * @param matchListMatch the match to download and save.
     */
    GameRunnable(Summoner summoner, RiotApi riotApi, MatchListMatch matchListMatch) {
        this.summoner = summoner;
        this.riotApi = riotApi;
        this.matchListMatch = matchListMatch;
        this.region = Region.parseRegionByPlatformId(matchListMatch.getPlatformId());
    }

    /**
     * Will get the game from the api and extract the important data.
     */
    @Override
    public void run() {
        try {
            Match match = this.riotApi.matchApi.getMatchById(this.matchListMatch.getGameId(), this.region);
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
            this.save(jsonGame, this.matchListMatch.getGameId());
            Logger.logMessage("Saving game: " + this.matchListMatch.getGameId() + " from " + this.summoner.getName(), LogLevel.INFO, LogReason.GG);
        } catch (RiotApiRequestException e) {
            if (e.getResponseCode().equals(ResponseCode.RATE_LIMIT_EXCEEDED)) {
                RateLimitException rateLimitException = (RateLimitException) e;
                try {
                    Thread.sleep(rateLimitException.getRetryAfter() * 1000 + 100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } else {
                Logger.logMessage("Riot api exception: " + e.getResponseCode() + " " + e.getMessage(), LogLevel.INFO, LogReason.GG);
                Logger.logMessage("Game: " + this.matchListMatch.getGameId() + " from: " + this.summoner.getId() + " in: " + this.region.getName(), LogLevel.INFO, LogReason.GG);
            }
        }
    }

    /**
     * Saves the formatted json to a file.
     *
     * @param jsonGame the formatted data.
     * @param gameId   the id under which the game gets saved.
     */
    private void save(JSONObject jsonGame, long gameId) {
        try {
            File file = new File("Z:/libs/resources/games/" + gameId + ".json");
            /* Use this for testing purposes. */
            //File file = new File("resources/games/" + gameId + ".json");
            if (!file.createNewFile()) {
                Logger.logMessage("File already exists. (" + gameId + ")", LogLevel.WARNING, LogReason.GG);
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(jsonGame.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
