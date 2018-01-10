package net.cerberus.sg.runnables;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.league.LeaguePosition;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import net.cerberus.sg.common.Summoner;
import net.cerberus.sg.common.Tier;
import net.cerberus.sg.db.DatabaseManager;

import java.util.Iterator;
import java.util.List;

public class SummonerUpdateRunnable implements Runnable {

    private Summoner summoner;
    private DatabaseManager databaseManager;
    private RiotApi riotApi;

    public SummonerUpdateRunnable(Summoner summoner, DatabaseManager databaseManager, RiotApi riotApi) {
        this.summoner = summoner;
        this.databaseManager = databaseManager;
        this.riotApi = riotApi;
    }

    @Override
    public void run() {
        try {
            List<LeaguePosition> leaguePositions = this.riotApi.leagueApi.getPositionBySummoner(this.summoner.getSummonerId(), Region.parseRegionByPlatformId(this.summoner.getPlatformId()));
            if (leaguePositions.stream().noneMatch(leaguePosition -> this.isTierAllowed(leaguePosition.getTier()))) {
                this.databaseManager.deleteSummoner(this.summoner.getSummonerDatabaseId());
            } else {
                this.summoner = this.getSummonerFromLeaguePosition(this.summoner.getSummonerId(), this.summoner.getSummonerDatabaseId(), Region.parseRegionByPlatformId(this.summoner.getPlatformId()), leaguePositions);
                this.databaseManager.updateSummoner(this.summoner);
            }
        } catch (RiotApiRequestException e) {
            switch (e.getResponseCode()) {
                case DATA_NOT_FOUND:
                    this.databaseManager.deleteSummoner(this.summoner.getSummonerDatabaseId());
                    this.databaseManager.deleteSummoner(this.summoner.getSummonerDatabaseId());
                    break;
                default:
                    e.getResponseCode();
                    e.printStackTrace();
                    break;
            }
        }
    }

    private Summoner getSummonerFromLeaguePosition(long summonerId, int summonerDatabaseId, Region region, List<LeaguePosition> leaguePositions) {
        Iterator<LeaguePosition> positions = leaguePositions.iterator();
        LeaguePosition highestLeaguePosition = positions.next();
        while (positions.hasNext()) {
            LeaguePosition leaguePosition = positions.next();
            if (Tier.parseTierByName(leaguePosition.getTier()).isTierHigherThan(Tier.parseTierByName(highestLeaguePosition.getTier()))) {
                highestLeaguePosition = leaguePosition;
            }
        }
        final LeaguePosition finalHighestLeaguePosition = highestLeaguePosition;
        return new Summoner() {
            @Override
            public String getPlatformId() {
                return region.getPlatformId();
            }

            @Override
            public long getSummonerId() {
                return summonerId;
            }

            @Override
            public String getRank() {
                return finalHighestLeaguePosition.getRank();
            }

            @Override
            public String getTier() {
                return finalHighestLeaguePosition.getTier();
            }

            @Override
            public int getSummonerDatabaseId() {
                return summonerDatabaseId;
            }
        };
    }

    private boolean isTierAllowed(String tier) {
        return tier.equals("PLATINUM") || tier.equals("CHALLENGER") || tier.equals("DIAMOND") || tier.equals("MASTER");
    }

}
