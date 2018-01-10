package net.cerberus.sg;

import net.cerberus.riotApi.api.RiotApi;
import net.cerberus.riotApi.common.constants.Region;
import net.cerberus.riotApi.exception.InvalidApiKeyException;
import net.cerberus.riotApi.exception.RiotApiRequestException;
import net.cerberus.sg.db.DatabaseManager;
import net.cerberus.sg.events.RiotApiListener;
import net.cerberus.sg.logger.LogLevel;
import net.cerberus.sg.logger.LogReason;
import net.cerberus.sg.logger.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        Logger.logMessage("Starting to gather summoners.", LogLevel.INFO, LogReason.SG);
        StringBuilder credentialsStringBuilder = new StringBuilder();
        try {
            BufferedReader credentialsBufferedReader = new BufferedReader(new FileReader(new File("resources/config.json")));
            credentialsBufferedReader.lines().forEach(credentialsStringBuilder::append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject credentials = new JSONObject(credentialsStringBuilder.toString());
        DatabaseManager databaseManager = new DatabaseManager(
                credentials.getJSONObject("sg-config").getJSONObject("database").getString("db_user"),
                credentials.getJSONObject("sg-config").getJSONObject("database").getString("db_pw"),
                credentials.getJSONObject("sg-config").getJSONObject("database").getString("host"),
                credentials.getJSONObject("sg-config").getJSONObject("database").getString("db")
        );
        try {
            Region region = Region.parseRegionByPlatformId(credentials.getJSONObject("sg-config").getString("summoner-region"));
            String summonerName = credentials.getJSONObject("sg-config").getString("summoner-name");
            Logger.logMessage("Starting with: " + summonerName + " from " + region.getName().toUpperCase(), LogLevel.INFO, LogReason.PARSER);
            RiotApi riotApi = new RiotApi(credentials.getString("key"), 3000);
            riotApi.getEventManager().registerEventListener(new RiotApiListener());
            DataFetcher dataFetcher = new DataFetcher(
                    riotApi,
                    riotApi.summonerApi.getSummonerByName(summonerName, region),
                    region,
                    credentials.getJSONObject("sg-config").getInt("updateBulk"),
                    credentials.getJSONObject("sg-config").getInt("saveBulk"),
                    databaseManager);
            if(credentials.getJSONObject("sg-config").getBoolean("updateAllSummoners")){
                dataFetcher.updateSummoners();
            }
            dataFetcher.start();
        } catch (InvalidApiKeyException | RiotApiRequestException e) {
            e.printStackTrace();
        }
    }
}
