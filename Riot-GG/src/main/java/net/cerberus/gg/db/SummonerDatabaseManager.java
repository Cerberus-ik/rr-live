package net.cerberus.gg.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.cerberus.riotApi.common.constants.Region;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SummonerDatabaseManager {

    private MysqlDataSource dataSource;

    public SummonerDatabaseManager(String userName, String password, String host, String db) {
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUser(userName);
        this.dataSource.setPassword(password);
        this.dataSource.setServerName(host);
        this.dataSource.setURL("jdbc:mysql://" + host + "/" + db + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    }

    public boolean isGameCached(long gameId, Region region) {
        boolean returnValue = false;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT gameId FROM games WHERE gameId = ? AND region = ?");
            preparedStatement.setLong(1, gameId);
            preparedStatement.setString(2, region.getPlatformId());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                returnValue = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
                preparedStatement.close();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }

    public void cacheGame(long gameId, Region region, JSONObject game) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO games (gameId, region, game, timestamp) VALUE (?, ?, ?, ?)")) {
            preparedStatement.setLong(1, gameId);
            preparedStatement.setString(2, region.getPlatformId());
            preparedStatement.setString(3, game.toString());
            preparedStatement.setLong(4, game.getJSONObject("game").getLong("timestamp"));
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
