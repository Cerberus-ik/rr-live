package net.cerberus.sg;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Queue;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.common.constants.ResponseCode;
import net.cerberus.riotApi.common.constants.Season;
import net.cerberus.riotApi.common.league.League;
import net.cerberus.riotApi.common.league.LeagueEntry;
import net.cerberus.riotApi.common.league.LeaguePosition;
import net.cerberus.riotApi.common.match.Match;
import net.cerberus.riotApi.common.match.MatchList;
import net.cerberus.riotApi.common.match.MatchListMatch;
import net.cerberus.riotApi.common.match.ParticipantIdentity;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import net.cerberus.sg.logs.LogLevel;
import net.cerberus.sg.logs.LogReason;
import net.cerberus.sg.logs.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataFetcher {

    private RiotApi riotApi;
    private Summoner summoner;
    private Region region;
    private JSONObject data;
    private List<Long> summonerIds;
    private List<String> leagueIds;
    private int cooldown;

    DataFetcher(RiotApi riotApi, Summoner summoner, Region region, JSONObject data, int cooldown) {
        this.riotApi = riotApi;
        this.summoner = summoner;
        this.region = region;
        this.data = data;
        this.summonerIds = new ArrayList<>();
        this.leagueIds = new ArrayList<>();
        this.cooldown = cooldown;
    }

    public void start(int limit) {
        this.readData();
        if (data.getJSONArray("data").length() >= limit) {
            Logger.logMessage("SG has already enough gathered summoners.", LogLevel.INFO, LogReason.SG);
            return;
        } else {
            Logger.logMessage("Targeting " + limit + " summoners.", LogLevel.INFO, LogReason.SG);
        }

        /* Gets the start users league */
        League league = this.getLeagueFromUser(this.summoner.getId());
        if (league == null) {
            Logger.logMessage("Base summoner is not in a league.", LogLevel.WARNING, LogReason.SG);
        } else if (this.isTierAllowed(league.getTier())) {
            this.leagueIds.add(league.getLeagueId());
        } else {
            Logger.logMessage("Base summoners solo league is to low.", LogLevel.WARNING, LogReason.SG);
        }
        try {
            Thread.sleep(this.cooldown);
            MatchList matchList = this.riotApi.matchApi.getMatchListByAccountId(this.summoner.getAccountId(), this.region, Queue.TEAM_BUILDER_RANKED_SOLO, null, null, null, null, Season.SEASON2017, null);
            for (MatchListMatch matchListMatch : matchList.getMatches()) {
                Thread.sleep(this.cooldown);
                Match match = this.riotApi.matchApi.getMatchById(matchListMatch.getGameId(), this.region);
                for (ParticipantIdentity participant : match.getParticipantIdentities()) {
                    Thread.sleep(this.cooldown);
                    this.riotApi.leagueApi.getPositionBySummoner(participant.getPlayer().getSummonerId(), this.region)
                            .stream()
                            .filter(leaguePosition -> this.isTierAllowed(leaguePosition.getTier()) && !this.leagueIds.contains(leaguePosition.getLeagueId()))
                            .forEach(leaguePosition -> {
                                this.leagueIds.add(leaguePosition.getLeagueId());
                                Logger.logMessage("Found a new league: " + leaguePosition.getLeagueId(), LogLevel.INFO, LogReason.SG);
                            });
                }
            }
        } catch (RiotApiRequestException | InterruptedException e) {
            e.printStackTrace();
        }
        this.parseLeagueMembersToJson();
        int finishingSize = this.data.getJSONArray("data").length();
        Logger.logMessage("Finished with " + finishingSize + " summoners. " + (Math.round((finishingSize / limit) * 100) / 100), LogLevel.INFO, LogReason.SG);
    }

    private void readData() {
        if (!this.data.has("data")) {
            this.data.put("data", new JSONArray());
        }
        for (int i = 0; i < this.data.getJSONArray("data").length(); i++) {
            this.summonerIds.add(this.data.getJSONArray("data").getJSONObject(i).getLong("summonerId"));
        }
        Logger.logMessage("Starting with " + this.data.getJSONArray("data").length() + " summoners.", LogLevel.INFO, LogReason.SG);
    }

    private void parseLeagueMembersToJson() {
        Logger.logMessage("Found " + this.leagueIds.size() + " leagues.", LogLevel.INFO, LogReason.SG);
        this.leagueIds.forEach(leagueId -> {
            try {
                Thread.sleep(this.cooldown);
                League league = this.riotApi.leagueApi.getLeagueById(leagueId, this.region);
                for (LeagueEntry leagueEntry : league.getEntries().stream().filter(leagueEntry -> !this.summonerIds.contains(Long.valueOf(leagueEntry.getPlayerOrTeamId()))).collect(Collectors.toList())) {
                    JSONObject summonerObject = new JSONObject();
                    summonerObject.put("summonerId", Long.valueOf(leagueEntry.getPlayerOrTeamId()));
                    summonerObject.put("platformId", this.region.getPlatformId());
                    summonerObject.put("rank", leagueEntry.getRank());
                    summonerObject.put("tier", league.getTier());
                    this.data.getJSONArray("data").put(summonerObject);
                }
            } catch (RiotApiRequestException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private League getLeagueFromUser(long summonerId) {
        try {
            Thread.sleep(this.cooldown);
            LeaguePosition leaguePosition = this.riotApi.leagueApi.getPositionBySummoner(summonerId, this.region)
                    .stream()
                    .filter(leaguePositionStream -> leaguePositionStream.getQueueType()
                            .equals(Queue.RANKED_SOLO_5x5.name()))
                    .findFirst().orElseThrow(NullPointerException::new);
            Thread.sleep(this.cooldown);
            return this.riotApi.leagueApi.getLeagueById(leaguePosition.getLeagueId(), this.region);
        } catch (RiotApiRequestException e) {
            if (!e.getResponseCode().equals(ResponseCode.INTERNAL_SERVER_ERROR) && !e.getResponseCode().equals(ResponseCode.DATA_NOT_FOUND)) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isTierAllowed(String tier) {
        return tier.equals("PLATINUM") || tier.equals("CHALLENGER") || tier.equals("DIAMOND") || tier.equals("MASTER");
    }
}
