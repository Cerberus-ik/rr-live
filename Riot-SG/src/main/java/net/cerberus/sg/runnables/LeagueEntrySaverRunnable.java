package net.cerberus.sg.runnables;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.league.League;
import net.cerberus.riotApi.common.league.LeagueEntry;
import net.cerberus.riotApi.common.league.LeaguePosition;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import net.cerberus.sg.common.Summoner;
import net.cerberus.sg.common.Tier;
import net.cerberus.sg.db.DatabaseManager;

import java.util.Iterator;
import java.util.List;

public class LeagueEntrySaverRunnable implements Runnable {

    private LeagueEntry leagueEntry;
    private League league;
    private Region region;
    private DatabaseManager databaseManager;

    public LeagueEntrySaverRunnable(LeagueEntry leagueEntry, League league, Region region, DatabaseManager databaseManager) {
        this.leagueEntry = leagueEntry;
        this.league = league;
        this.region = region;
        this.databaseManager = databaseManager;
    }

    @Override
    public void run() {
        if(this.databaseManager.isSummonerCached(Long.parseLong(this.leagueEntry.getPlayerOrTeamId()), this.region.getPlatformId())){
            return;
        }
        this.databaseManager.saveSummoner(new Summoner() {
            @Override
            public String getPlatformId() {
                return region.getPlatformId();
            }

            @Override
            public long getSummonerId() {
                return Long.parseLong(leagueEntry.getPlayerOrTeamId());
            }

            @Override
            public String getRank() {
                return leagueEntry.getRank();
            }

            @Override
            public String getTier() {
                return league.getTier();
            }

            @Override
            public int getSummonerDatabaseId() {
                return -1;
            }
        });
    }
}
