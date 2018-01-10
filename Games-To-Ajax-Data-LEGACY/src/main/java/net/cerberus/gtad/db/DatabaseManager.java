package net.cerberus.gtad.db;

import net.cerberus.gtad.io.logger.LogLevel;
import net.cerberus.gtad.io.logger.LogReason;
import net.cerberus.gtad.io.logger.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    private Database database;

    public DatabaseManager() {
    }

    public void connect() {
        Logger.logMessage("Connecting to db.", LogLevel.INFO, LogReason.DB);
        this.database = new Database(System.getenv("DB_URL"), 3306, System.getenv("DB_DB"), System.getenv("DB_USER"), System.getenv("DB_PW"));
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

    public void truncateDatabase() {
        try {
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("TRUNCATE `rr-live`");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Logger.logMessage("Truncated database.", LogLevel.INFO, LogReason.DB);
    }

    public void updateId(int id, String content) {
        try {
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("INSERT INTO `rr-live` (id, content) VALUES(?, ?);");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, content);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
