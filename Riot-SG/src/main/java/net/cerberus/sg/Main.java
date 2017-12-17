package net.cerberus.sg;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.Summoner;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.exception.InvalidApiKeyException;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import net.cerberus.sg.logs.LogLevel;
import net.cerberus.sg.logs.LogReason;
import net.cerberus.sg.logs.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        Logger.logMessage("Starting to gather summoners.", LogLevel.INFO, LogReason.SG);
        StringBuilder credentialsStringBuilder = new StringBuilder();
        StringBuilder summonerStringBuilder = new StringBuilder();
        try {
            BufferedReader credentialsBufferedReader = new BufferedReader(new FileReader(new File("resources/config.json")));
            BufferedReader summonersBufferedReader = new BufferedReader(new FileReader(new File("resources/sg-summoners.json")));
            summonersBufferedReader.lines().forEach(summonerStringBuilder::append);
            credentialsBufferedReader.lines().forEach(credentialsStringBuilder::append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject credentials = new JSONObject(credentialsStringBuilder.toString());
        JSONObject summoners = new JSONObject(summonerStringBuilder.toString());
        try {
            RiotApi riotApi = new RiotApi(credentials.getString("key"), 5000);
            Region region = Region.parseRegionByPlatformId(credentials.getJSONObject("sg-config").getString("summoner-region"));
            Summoner summoner = riotApi.summonerApi.getSummonerByName(credentials.getJSONObject("sg-config").getString("summoner-name"), region);
            DataFetcher dataFetcher = new DataFetcher(riotApi, summoner, region, summoners, credentials.getJSONObject("sg-config").getInt("coolDownBetweenCalls"));
            dataFetcher.start(credentials.getJSONObject("sg-config").getInt("summonerLimit"));
        } catch (RiotApiRequestException e) {
            System.out.println(e.getResponseCode());
            e.printStackTrace();
        } catch (InvalidApiKeyException e) {
            e.printStackTrace();
        }
    }
}
