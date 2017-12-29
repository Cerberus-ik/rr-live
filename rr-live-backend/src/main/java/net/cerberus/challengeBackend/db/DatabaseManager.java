package net.cerberus.challengeBackend.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.cerberus.challengeBackend.api.v1.cachingObjects.*;
import net.cerberus.challengeBackend.io.logs.LogLevel;
import net.cerberus.challengeBackend.io.logs.LogReason;
import net.cerberus.challengeBackend.io.logs.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseManager {

    private MysqlDataSource dataSource;

    public DatabaseManager(DatabaseCredentials databaseCredentials) {
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUser(databaseCredentials.getUser());
        this.dataSource.setPassword(databaseCredentials.getPassword());
        this.dataSource.setServerName(databaseCredentials.getHost());
        this.dataSource.setPort(databaseCredentials.getPort());
        this.dataSource.setURL("jdbc:mysql://" + databaseCredentials.getHost() + "/" + databaseCredentials.getDatabase() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    }

    public List<String> getIdObject(int id, int limit) {
        try {
            PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement("SELECT content FROM `backend-api` WHERE id >= ? ORDER BY id LIMIT ?;");
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> resultString = new ArrayList<>();
            while (resultSet.next()) {
                resultString.add(resultSet.getString(1));
            }
            resultSet.close();
            preparedStatement.close();
            return resultString;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Logger.logMessage("Invalid id requested", LogLevel.WARNING, LogReason.DB);
        return new ArrayList<>();
    }

    public int getLatestStepId() {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM `backend-api` ORDER BY id DESC LIMIT 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            int result = -1;
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            connection.close();
            preparedStatement.close();
            resultSet.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public LeastPickedRuneCache getLeastPickedRune() {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT content FROM `backend-api`");
            ResultSet resultSet = preparedStatement.executeQuery();
            HashMap<Integer, Integer> runesPicked = new HashMap<>();
            while (resultSet.next()) {
                JSONObject content = new JSONObject(resultSet.getString(1));
                JSONArray adc = content.getJSONArray("ADC");
                JSONArray mid = content.getJSONArray("MID");
                JSONArray top = content.getJSONArray("TOP");
                JSONArray support = content.getJSONArray("SUPPORT");
                JSONArray jungle = content.getJSONArray("JUNGLE");
                for (int i = 0; i < adc.length(); i++) {
                    int key = Integer.parseInt(adc.getJSONObject(i).keys().next());
                    runesPicked.put(key, runesPicked.getOrDefault(key, 0) + adc.getJSONObject(i).getInt(String.valueOf(key)));
                }
                for (int i = 0; i < mid.length(); i++) {
                    int key = Integer.parseInt(mid.getJSONObject(i).keys().next());
                    runesPicked.put(key, runesPicked.getOrDefault(key, 0) + mid.getJSONObject(i).getInt(String.valueOf(key)));
                }
                for (int i = 0; i < top.length(); i++) {
                    int key = Integer.parseInt(top.getJSONObject(i).keys().next());
                    runesPicked.put(key, runesPicked.getOrDefault(key, 0) + top.getJSONObject(i).getInt(String.valueOf(key)));
                }
                for (int i = 0; i < support.length(); i++) {
                    int key = Integer.parseInt(support.getJSONObject(i).keys().next());
                    runesPicked.put(key, runesPicked.getOrDefault(key, 0) + support.getJSONObject(i).getInt(String.valueOf(key)));
                }
                for (int i = 0; i < jungle.length(); i++) {
                    int key = Integer.parseInt(jungle.getJSONObject(i).keys().next());
                    runesPicked.put(key, runesPicked.getOrDefault(key, 0) + jungle.getJSONObject(i).getInt(String.valueOf(key)));
                }
            }
            LeastPickedRuneCache cache = new LeastPickedRuneCache(999999999, 999999999);
            for (Integer key : runesPicked.keySet()) {
                if (runesPicked.get(key) < cache.getTimesPicked()) {
                    cache = new LeastPickedRuneCache(runesPicked.get(key), key);
                }
            }
            connection.close();
            preparedStatement.close();
            resultSet.close();
            return cache;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getAnalyzedRunes() {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT content FROM `backend-api`");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<JSONObject> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(new JSONObject(resultSet.getString(1)));
            }
            long i = 0;
            for (JSONObject id : ids) {
                for (int runesPerRoleId = 0; runesPerRoleId < id.getJSONArray("ADC").length(); runesPerRoleId++) {
                    i = i + id.getJSONArray("ADC").getJSONObject(runesPerRoleId).getInt(id.getJSONArray("ADC").getJSONObject(runesPerRoleId).keys().next());
                }
            }
            connection.close();
            preparedStatement.close();
            resultSet.close();
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public MostDominantRuneCache getMostDominantRune() {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT content FROM `backend-api`");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<JSONObject> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(new JSONObject(resultSet.getString(1)));
            }
            MostDominantRuneCache mostDominantRuneCache = this.getIdRuneCache(ids.get(0), this.getIdByContent(ids.get(0)));
            for (MostDominantRuneCache cache : ids.stream().map(id -> this.getIdRuneCache(id, this.getIdByContent(id))).collect(Collectors.toList())) {
                if (mostDominantRuneCache.getTimesPicked() / mostDominantRuneCache.getTotalRunesPicked() > cache.getTimesPicked() / cache.getTotalRunesPicked()) {
                    mostDominantRuneCache = cache;
                }
            }
            connection.close();
            preparedStatement.close();
            resultSet.close();
            return mostDominantRuneCache;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getIdByContent(JSONObject jsonObject) {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM `backend-api` WHERE content = ?");
            preparedStatement.setString(1, jsonObject.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            int result = -1;
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
            connection.close();
            resultSet.close();
            preparedStatement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int totalRunesInArray(JSONArray jsonArray) {
        int amount = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            amount = amount + jsonArray.getJSONObject(i).getInt(jsonArray.getJSONObject(i).keys().next());
        }
        return amount;
    }

    private MostDominantRuneCache getIdRuneCache(JSONObject idContent, int timeId) {
        List<MostDominantRuneCache> list = new ArrayList<>();
        list.add(this.getIdRoleRuneCache(idContent.getJSONArray("ADC"), "ADC", timeId));
        list.add(this.getIdRoleRuneCache(idContent.getJSONArray("JUNGLE"), "JUNGLE", timeId));
        list.add(this.getIdRoleRuneCache(idContent.getJSONArray("MID"), "MID", timeId));
        list.add(this.getIdRoleRuneCache(idContent.getJSONArray("TOP"), "TOP", timeId));
        list.add(this.getIdRoleRuneCache(idContent.getJSONArray("SUPPORT"), "SUPPORT", timeId));

        MostDominantRuneCache mostDominantRuneCache = list.get(0);
        list.remove(0);
        for (MostDominantRuneCache cache : list) {
            if (mostDominantRuneCache.getTimesPicked() / mostDominantRuneCache.getTotalRunesPicked() > cache.getTimesPicked() / cache.getTotalRunesPicked()) {
                mostDominantRuneCache = cache;
            }
        }
        return mostDominantRuneCache;
    }

    private MostDominantRuneCache getIdRoleRuneCache(JSONArray jsonArray, String role, int timeId) {
        double topPercentage = 0;
        int totalRunes = this.totalRunesInArray(jsonArray);
        int timesPicked = 0;
        int runeId = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            double percentage = (double) jsonArray.getJSONObject(i).getInt(jsonArray.getJSONObject(i).keys().next()) / (double) totalRunes;
            if (percentage > topPercentage) {
                topPercentage = percentage;
                timesPicked = jsonArray.getJSONObject(i).getInt(jsonArray.getJSONObject(i).keys().next());
                runeId = Integer.parseInt(jsonArray.getJSONObject(i).keys().next());
            }
        }
        return new MostDominantRuneCache(timesPicked, totalRunes, timeId, runeId, role);
    }

    public MostDominantRunesCache getMostDominantRunesPerTimeIds() {
        List<MostDominantRunesPerTimeId> mostDominantRuneCaches = new ArrayList<>();
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT content FROM `backend-api`");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<JSONObject> ids = new ArrayList<>();
            while (resultSet.next()) {
                ids.add(new JSONObject(resultSet.getString(1)));
            }
            List<Integer> runeOrder = new ArrayList<>();
            for (int i = 0; i < ids.get(0).getJSONArray("runeOrder").length(); i++) {
                runeOrder.add(ids.get(0).getJSONArray("runeOrder").getInt(i));
            }
            for (JSONObject id : ids) {
                HashMap<String, RoleRune> hashMap = new HashMap<>();
                for (int i = 0; i < id.getJSONArray("ADC").length(); i++) {
                    int runeId = runeOrder.get(i);
                    if (id.getJSONArray("ADC").getJSONObject(i).has(String.valueOf(runeOrder.get(i)))) {
                        hashMap.put("ADC", new RoleRune("ADC", runeId, id.getJSONArray("ADC").getJSONObject(i).getInt(String.valueOf(runeOrder.get(i)))));
                    }
                }
                for (int i = 0; i < id.getJSONArray("JUNGLE").length(); i++) {
                    int runeId = runeOrder.get(i);
                    if (id.getJSONArray("JUNGLE").getJSONObject(i).has(String.valueOf(runeOrder.get(i)))) {
                        hashMap.put("JUNGLE", new RoleRune("JUNGLE", runeId, id.getJSONArray("JUNGLE").getJSONObject(i).getInt(String.valueOf(runeId))));
                    }
                }
                for (int i = 0; i < id.getJSONArray("MID").length(); i++) {
                    int runeId = runeOrder.get(i);
                    if (id.getJSONArray("MID").getJSONObject(i).has(String.valueOf(runeOrder.get(i)))) {
                        hashMap.put("MID", new RoleRune("MID", runeId, id.getJSONArray("MID").getJSONObject(i).getInt(String.valueOf(runeId))));
                    }
                }
                for (int i = 0; i < id.getJSONArray("SUPPORT").length(); i++) {
                    int runeId = runeOrder.get(i);
                    if (id.getJSONArray("SUPPORT").getJSONObject(i).has(String.valueOf(runeOrder.get(i)))) {
                        hashMap.put("SUPPORT", new RoleRune("SUPPORT", runeId, id.getJSONArray("SUPPORT").getJSONObject(i).getInt(String.valueOf(runeId))));
                    }
                }
                for (int i = 0; i < id.getJSONArray("TOP").length(); i++) {
                    int runeId = runeOrder.get(i);
                    if (id.getJSONArray("TOP").getJSONObject(i).has(String.valueOf(runeOrder.get(i)))) {
                        hashMap.put("TOP", new RoleRune("TOP", runeId, id.getJSONArray("TOP").getJSONObject(i).getInt(String.valueOf(runeId))));
                    }
                }
                mostDominantRuneCaches.add(new MostDominantRunesPerTimeId(hashMap, id.getInt("id")));
            }
            connection.close();
            preparedStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MostDominantRunesCache(mostDominantRuneCaches);
    }
}
