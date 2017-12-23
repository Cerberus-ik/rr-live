package net.cerberus.gg;

import net.cerberus.gg.db.DatabaseManager;
import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.match.Match;
import net.cerberus.riotApi.common.match.MatchListMatch;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import org.json.JSONObject;

public class MatchRunnable implements Runnable {

    private MatchListMatch matchListMatch;
    private RiotApi riotApi;
    private Region region;
    private DatabaseManager databaseManager;

    MatchRunnable(MatchListMatch matchListMatch, RiotApi riotApi, DatabaseManager databaseManager) {
        this.matchListMatch = matchListMatch;
        this.riotApi = riotApi;
        this.region = Region.parseRegionByPlatformId(matchListMatch.getPlatformId());
        this.databaseManager = databaseManager;
    }

    /**
     * Will get all appropriate matches from a user and save them to the list.
     */
    @Override
    public void run() {
        try {
            Match match = this.riotApi.matchApi.getMatchById(matchListMatch.getGameId(), Region.parseRegionByPlatformId(matchListMatch.getPlatformId()));
            JSONObject gameObject = this.parseObject(match);
            this.databaseManager.cacheGame(match.getGameId(), this.region, gameObject);
        } catch (RiotApiRequestException e) {
            System.out.println(this.matchListMatch.getGameId());
            System.out.println(e.getResponseCode());
            e.printStackTrace();
        }
    }

    private JSONObject parseObject(Match match) {
        JSONObject jsonGame = new JSONObject();
        JSONObject dataObject = new JSONObject();
        dataObject.put("timestamp", match.getGameCreation());
        dataObject.put("patch", match.getGameVersion());
        dataObject.put("region", match.getPlatformId());
        jsonGame.put("game", dataObject);
        match.getParticipants().forEach(participant -> {
            jsonGame.put(participant.getParticipantId() + "-role", participant.getTimeline().getRole());
            jsonGame.put(participant.getParticipantId() + "-perk0", participant.getStats().getPerk0());
            jsonGame.put(participant.getParticipantId() + "-perk1", participant.getStats().getPerk1());
            jsonGame.put(participant.getParticipantId() + "-perk2", participant.getStats().getPerk2());
            jsonGame.put(participant.getParticipantId() + "-perk3", participant.getStats().getPerk3());
            jsonGame.put(participant.getParticipantId() + "-perk4", participant.getStats().getPerk4());
            jsonGame.put(participant.getParticipantId() + "-perk5", participant.getStats().getPerk5());
            jsonGame.put(participant.getParticipantId() + "-lane", participant.getTimeline().getLane());
        });
        return jsonGame;
    }
}
