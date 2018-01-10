package net.cerberus.sg.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.cerberus.sg.common.Summoner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private MysqlDataSource dataSource;

    public DatabaseManager(String userName, String password, String host, String db) {
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUser(userName);
        this.dataSource.setPassword(password);
        this.dataSource.setServerName(host);
        this.dataSource.setURL("jdbc:mysql://" + host + "/" + db + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    }

    public List<Summoner> getSummoners() {
        List<Summoner> summoners = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT platformId, summonerId, rank, tier, playerId FROM summoners")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String platformId = resultSet.getString(1);
                long summonerId = resultSet.getLong(2);
                String rank = resultSet.getString(3);
                String tier = resultSet.getString(4);
                int summonerDatabaseId = resultSet.getInt(5);
                summoners.add(new Summoner() {
                    @Override
                    public String getPlatformId() {
                        return platformId;
                    }

                    @Override
                    public long getSummonerId() {
                        return summonerId;
                    }

                    @Override
                    public String getRank() {
                        return rank;
                    }

                    @Override
                    public String getTier() {
                        return tier;
                    }

                    @Override
                    public int getSummonerDatabaseId() {
                        return summonerDatabaseId;
                    }
                });
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summoners;
    }

    public void updateSummoner(Summoner summoner){
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE summoners SET platformId = ?, summonerId = ?, rank = ?, tier = ? WHERE playerId = ?")) {
            preparedStatement.setString(1, summoner.getPlatformId());
            preparedStatement.setLong(2, summoner.getSummonerId());
            preparedStatement.setString(3, summoner.getRank());
            preparedStatement.setString(4, summoner.getTier());
            preparedStatement.setInt(5, summoner.getSummonerDatabaseId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSummoner(Summoner summoner) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO summoners (platformId, summonerId, rank, tier) VALUE (?, ?, ?, ?)")) {
            preparedStatement.setString(1, summoner.getPlatformId());
            preparedStatement.setLong(2, summoner.getSummonerId());
            preparedStatement.setString(3, summoner.getRank());
            preparedStatement.setString(4, summoner.getTier());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSummoner(int summonerDbId) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM summoners WHERE platformId = ?;")) {
            preparedStatement.setInt(1, summonerDbId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isSummonerCached(int summonerDbId){
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT playerId FROM summoners WHERE playerId = ?")) {
            preparedStatement.setInt(1, summonerDbId);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean result = resultSet.next();
            preparedStatement.close();
            connection.close();
            resultSet.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSummonerCached(long summonerId, String platformId){
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT playerId FROM summoners WHERE summonerId = ? AND platformId = ?")) {
            preparedStatement.setLong(1, summonerId);
            preparedStatement.setString(2, platformId);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean result = resultSet.next();
            preparedStatement.close();
            connection.close();
            resultSet.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
