package net.cerberus.gg;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.exception.RiotApiRequestException;

import java.util.HashMap;

public class SummonerRunnable implements Runnable {

    private long summonerId;
    private RiotApi riotApi;
    private Region region;
    private HashMap<Summoner, Region> summoners;

    SummonerRunnable(long summonerId, RiotApi riotApi, Region region, HashMap<Summoner, Region> summoners) {
        this.summonerId = summonerId;
        this.riotApi = riotApi;
        this.region = region;
        this.summoners = summoners;
    }

    /**
     * Will get the summoner from the api and save it to the list.
     */
    @Override
    public void run() {
        try {
            this.summoners.put(this.riotApi.summonerApi.getSummonerById(this.summonerId, this.region), this.region);
        } catch (RiotApiRequestException e) {
            e.printStackTrace();
        }
    }

}
