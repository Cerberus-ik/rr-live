package net.cerberus.gg;

import net.cerberus.gg.db.DatabaseManager;
import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Queue;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.constants.Season;
import net.cerberus.riotApi.common.match.MatchListMatch;
import net.cerberus.riotApi.exception.RiotApiRequestException;

import java.util.List;
import java.util.stream.Collectors;

public class MatchListRunnable implements Runnable {

    private Summoner summoner;
    private RiotApi riotApi;
    private Region region;
    private List<MatchListMatch> matches;
    private DatabaseManager databaseManager;

    MatchListRunnable(Summoner summoner, RiotApi riotApi, Region region, List<MatchListMatch> matches, DatabaseManager databaseManager) {
        this.summoner = summoner;
        this.riotApi = riotApi;
        this.region = region;
        this.matches = matches;
        this.databaseManager = databaseManager;
    }

    /**
     * Will get all appropriate matches from a user and save them to the list.
     */
    @Override
    public void run() {
        try {
            this.matches.addAll(this.riotApi.matchApi.getMatchListByAccountId(
                    summoner.getAccountId(),
                    this.region,
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
                                    !databaseManager.isGameCached(matchListMatch.getGameId(), region)
                                            && matchListMatch.getTimestamp() > 1511568000000L)
                    .collect(Collectors.toList()));
        } catch (RiotApiRequestException e) {
            System.out.println(e.getResponseCode());
            e.printStackTrace();
        }
    }
}
