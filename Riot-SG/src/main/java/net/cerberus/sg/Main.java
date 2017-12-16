package net.cerberus.sg;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.exception.InvalidApiKeyException;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        StringBuilder credentialsStringBuilder = new StringBuilder();
        StringBuilder summonerStringBuilder = new StringBuilder();
        try {
            BufferedReader credentialsBufferedReader = new BufferedReader(new FileReader(new File("Riot-SG/resources/credentials.json")));
            BufferedReader summonersBufferedReader = new BufferedReader(new FileReader(new File("Riot-SG/resources/summoners.json")));
            summonersBufferedReader.lines().forEach(summonerStringBuilder::append);
            credentialsBufferedReader.lines().forEach(credentialsStringBuilder::append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject credentials = new JSONObject(credentialsStringBuilder.toString());
        JSONObject summoners = new JSONObject(summonerStringBuilder.toString());
        try {
            RiotApi riotApi = new RiotApi(credentials.getString("api-key"), 5000);
            Region region = Region.parseRegionByPlatformId(credentials.getString("summoner-region"));
            Summoner summoner = riotApi.summonerApi.getSummonerByName(credentials.getString("summoner-name"), region);
            DataFetcher dataFetcher = new DataFetcher(riotApi, summoner, region, summoners);
            dataFetcher.start(5000);
        } catch (InvalidApiKeyException | RiotApiRequestException e) {
            e.printStackTrace();
        }
    }
}
