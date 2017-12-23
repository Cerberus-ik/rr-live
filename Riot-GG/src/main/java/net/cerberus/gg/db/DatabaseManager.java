package net.cerberus.gg.db;


import net.cerberus.gg.io.logs.LogLevel;
import net.cerberus.gg.io.logs.LogReason;
import net.cerberus.gg.io.logs.Logger;
import net.cerberus.riotApi.common.constants.Region;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    private Database database;
    private String host;
    private String db;
    private String user;
    private String pw;

    public DatabaseManager(String host, String db, String user, String pw) {
        this.host = host;
        this.db = db;
        this.user = user;
        this.pw = pw;
    }

    public void connect() {
        Logger.logMessage("Connecting to db.", LogLevel.INFO, LogReason.DB);
        this.database = new Database(this.host, 3306, this.db, this.user, this.pw);
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        this.database.connect();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            this.database.closeConnection();
            this.database.connect();
        }, 30, 30, TimeUnit.MINUTES);
    }

    public void closeConnection() {
        this.database.closeConnection();
    }

    public void cacheGame(long id, Region region, JSONObject game) {
        try {
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("INSERT INTO games (gameId, region, game, timestamp) VALUE (?, ?, ?, ?)");
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, region.getPlatformId());
            preparedStatement.setString(3, game.toString());
            preparedStatement.setLong(4, game.getJSONObject("game").getLong("timestamp"));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean gameAlreadyCached(long id, Region region) {
        try {
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("SELECT gameId FROM games WHERE gameId = ? AND region = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, region.getPlatformId());
            ResultSet results = preparedStatement.executeQuery();
            if (results.next()) {
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
