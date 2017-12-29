package net.cerberus.gtad.db;

import net.cerberus.gtad.common.DatabaseCredentials;
import net.cerberus.gtad.io.logs.LogLevel;
import net.cerberus.gtad.io.logs.LogReason;
import net.cerberus.gtad.io.logs.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TargetDatabaseManager {

    private Database database;
    private DatabaseCredentials databaseCredentials;
    private String table;

    public TargetDatabaseManager(DatabaseCredentials databaseCredentials, String table) {
        this.databaseCredentials = databaseCredentials;
        this.table = table;
    }

    public void connect() {
        Logger.logMessage("Connecting to target db.", LogLevel.INFO, LogReason.DB);
        this.database = new Database(this.databaseCredentials);
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
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("TRUNCATE TABLE `backend-api`");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Logger.logMessage("Truncated database.", LogLevel.INFO, LogReason.DB);
    }

    public void updateId(int id, String content) {
        try {
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("INSERT INTO `backend-api` (id, content) VALUES(?, ?);");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, content);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
