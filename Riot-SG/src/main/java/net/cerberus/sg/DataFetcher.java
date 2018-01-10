package net.cerberus.sg;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Queue;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.constants.Season;
import net.cerberus.riotApi.common.league.League;
import net.cerberus.riotApi.common.match.Match;
import net.cerberus.riotApi.common.match.MatchList;
import net.cerberus.riotApi.common.match.MatchListMatch;
import net.cerberus.riotApi.common.match.ParticipantIdentity;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import net.cerberus.sg.db.DatabaseManager;
import net.cerberus.sg.logger.LogLevel;
import net.cerberus.sg.logger.LogReason;
import net.cerberus.sg.logger.Logger;
import net.cerberus.sg.runnables.LeagueEntrySaverRunnable;
import net.cerberus.sg.runnables.SummonerUpdateRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DataFetcher {

    private RiotApi riotApi;
    private Summoner summoner;
    private Region region;
    private List<net.cerberus.sg.common.Summoner> summoners;
    private DatabaseManager databaseManager;
    private int updateBulk;
    private int saveBulk;

    DataFetcher(RiotApi riotApi, Summoner summoner, Region region, int updateBulk, int saveBulk, DatabaseManager databaseManager) {
        this.riotApi = riotApi;
        this.summoner = summoner;
        this.region = region;
        this.updateBulk = updateBulk;
        this.saveBulk = saveBulk;
        this.databaseManager = databaseManager;
        this.summoners = this.databaseManager.getSummoners();
    }

    public void start() {
        List<String> leagueIds = new ArrayList<>();
        Logger.logMessage("Starting with " + summoners.size() + " summoners.", LogLevel.INFO, LogReason.PARSER);
        Logger.logMessage("Updating summoners...", LogLevel.INFO, LogReason.PARSER);
        try {
            MatchList matchList = this.riotApi.matchApi.getMatchListByAccountId(this.summoner.getAccountId(), this.region, Queue.TEAM_BUILDER_RANKED_SOLO, null, null, null, null, Season.SEASON2017, null);
            for (MatchListMatch matchListMatch : matchList.getMatches()) {
                Match match = this.riotApi.matchApi.getMatchById(matchListMatch.getGameId(), this.region);
                for (ParticipantIdentity participant : match.getParticipantIdentities()) {
                    try {
                        this.riotApi.leagueApi.getPositionBySummoner(participant.getPlayer().getSummonerId(), this.region)
                                .stream()
                                .filter(leaguePosition -> this.isTierAllowed(leaguePosition.getTier()) && !leagueIds.contains(leaguePosition.getLeagueId()))
                                .forEach(leaguePosition -> {
                                    leagueIds.add(leaguePosition.getLeagueId());
                                    Logger.logMessage("Found a new league: " + leaguePosition.getLeagueId(), LogLevel.INFO, LogReason.SG);
                                });
                    }catch (RiotApiRequestException e){
                        e.printStackTrace();
                        e.getResponseCode();
                    }
                }
            }
        } catch (RiotApiRequestException e) {
            e.printStackTrace();
            e.getResponseCode();
        }
        this.saveSummoners(leagueIds);
    }

    private void saveSummoners(List<String> leagueIds) {
        Logger.logMessage("Saving summoners....", LogLevel.INFO, LogReason.PARSER);
        for (int i = 0; i < leagueIds.size(); i++) {
            try {
                League league = this.riotApi.leagueApi.getLeagueById(leagueIds.get(i), this.region);
                ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(this.saveBulk);
                league.getEntries().forEach(leagueEntry -> scheduledExecutorService.execute(new LeagueEntrySaverRunnable(leagueEntry, league, this.region, this.databaseManager)));
                scheduledExecutorService.awaitTermination(30, TimeUnit.SECONDS);
                Logger.logMessage("Finished with league: " + i + "/" + leagueIds.size(), LogLevel.INFO, LogReason.PARSER);
            } catch (RiotApiRequestException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSummoners() {
        Logger.logMessage("Updating " + this.updateBulk + " users at a time.", LogLevel.INFO, LogReason.PARSER);
        Iterator<net.cerberus.sg.common.Summoner> summonerIterator = this.summoners.iterator();
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(this.updateBulk);
        while (summonerIterator.hasNext()) {
            scheduledExecutorService.execute(new SummonerUpdateRunnable(summonerIterator.next(), this.databaseManager, this.riotApi));
        }
        try {
            scheduledExecutorService.awaitTermination(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isTierAllowed(String tier) {
        return tier.equals("PLATINUM") || tier.equals("CHALLENGER") || tier.equals("DIAMOND") || tier.equals("MASTER");
    }

}
