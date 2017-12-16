package net.cerberus.sg;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Queue;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.constants.Season;
import net.cerberus.riotApi.common.league.League;
import net.cerberus.riotApi.common.league.LeaguePosition;
import net.cerberus.riotApi.common.match.Match;
import net.cerberus.riotApi.common.match.MatchList;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataFetcher {

    private RiotApi riotApi;
    private Summoner summoner;
    private Region region;
    private JSONObject data;
    private List<Long> summonerIds;
    private List<String> leagueIds;

    DataFetcher(RiotApi riotApi, Summoner summoner, Region region, JSONObject data) {
        this.riotApi = riotApi;
        this.summoner = summoner;
        this.region = region;
        this.data = data;
        this.summonerIds = new ArrayList<>();
        this.leagueIds = new ArrayList<>();
    }

    public void start(int limit) {
        LeaguePosition leaguePosition;
        try {
            /* Gets the start users league */
            Thread.sleep(2000);
            leaguePosition = this.riotApi.leagueApi.getPositionBySummoner(this.summoner.getId(), this.region)
                    .stream()
                    .filter(leaguePositionStream -> leaguePositionStream.getQueueType()
                            .equals(Queue.RANKED_SOLO_5x5.name()))
                    .findFirst().orElseThrow(NullPointerException::new);
            Thread.sleep(2000);
            League league = this.riotApi.leagueApi.getLeagueById(leaguePosition.getLeagueId(), this.region);
            this.leagueIds.add(league.getLeagueId());
            /* Loops through every entry to find more summoners */
            league.getEntries().forEach(leagueEntry -> {
                /* Adds them since they have to be in the same Tier as the start summoner */
                this.summonerIds.add(Long.valueOf(leagueEntry.getPlayerOrTeamId()));
                System.out.println("Found summoner: " + leagueEntry.getPlayerOrTeamName());
                try {
                    /* Parses the summoner to get their match list */
                    Thread.sleep(2000);
                    Summoner summoner = this.riotApi.summonerApi.getSummonerById(Long.parseLong(leagueEntry.getPlayerOrTeamId()), region);
                    Thread.sleep(2000);
                    MatchList matchList = this.riotApi.matchApi.getMatchListByAccountId(summoner.getAccountId(), this.region, Queue.RANKED_SOLO_5x5, null, null, null, null, Season.SEASON2017, null);
                    /* Loops though every ranked match from the summoner */
                    matchList.getMatches().forEach(matchListMatch -> {
                        try {
                            Thread.sleep(2000);
                            Match match = this.riotApi.matchApi.getMatchById(matchListMatch.getGameId(), this.region);
                            /* Loops though every summoner the player played with in ranked games. */
                            match.getParticipantIdentities()
                                    .stream()
                                    .filter(participantIdentity -> !this.summonerIds.contains(participantIdentity.getPlayer().getSummonerId()))
                                    .forEach(participantIdentity -> {
                                        try {
                                            /* Checks every league for more players. */
                                            Thread.sleep(2000);
                                            LeaguePosition subLeaguePosition = this.riotApi.leagueApi.getPositionBySummoner(participantIdentity.getPlayer().getSummonerId(), this.region)
                                                    .stream()
                                                    .filter(leaguePositionStream -> leaguePositionStream.getQueueType()
                                                            .equals(Queue.RANKED_SOLO_5x5.name())
                                                            && !this.leagueIds.contains(leaguePosition.getLeagueId())
                                                            && this.tierAllowed(leaguePosition.getTier()))
                                                    .findFirst().orElse(null);
                                            if (subLeaguePosition == null) {
                                                return;
                                            }
                                            Thread.sleep(2000);
                                            League subLeague = this.riotApi.leagueApi.getLeagueById(subLeaguePosition.getLeagueId(), this.region);
                                            subLeague.getEntries()
                                                    .stream()
                                                    .filter(subLeagueEntry -> !this.leagueIds.contains(subLeagueEntry.getPlayerOrTeamId()))
                                                    .forEach(subLeagueEntry -> {
                                                        this.leagueIds.add(subLeagueEntry.getPlayerOrTeamId());
                                                        System.out.println("Found summoner: " + subLeagueEntry.getPlayerOrTeamName());
                                                    });
                                        } catch (RiotApiRequestException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    });
                        } catch (RiotApiRequestException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (RiotApiRequestException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (RiotApiRequestException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean tierAllowed(String tier) {
        return tier.equals("PLATINUM") || tier.equals("CHALLENGER") || tier.equals("DIAMOND") || tier.equals("MASTER");
    }
}
