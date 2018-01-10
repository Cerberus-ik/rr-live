package net.cerberus.gtad.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.cerberus.gtad.common.DatabaseCredentials;
import net.cerberus.gtad.io.logger.LogLevel;
import net.cerberus.gtad.io.logger.LogReason;
import net.cerberus.gtad.io.logger.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TargetDatabaseManager {

    private MysqlDataSource dataSource;

    public TargetDatabaseManager(DatabaseCredentials databaseCredentials) {
        Logger.logMessage("Connecting to target db.", LogLevel.INFO, LogReason.DB);
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUser(databaseCredentials.getUser());
        this.dataSource.setPassword(databaseCredentials.getPassword());
        this.dataSource.setServerName(databaseCredentials.getHost());
        this.dataSource.setURL("jdbc:mysql://" + databaseCredentials.getHost() + "/" + databaseCredentials.getDatabase() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    }

    public void truncateDatabase() {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE `backend-api`");
            preparedStatement.executeUpdate();
            connection.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Logger.logMessage("Truncated the database.", LogLevel.INFO, LogReason.DB);
    }

    public void updateId(int id, String content) {
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `backend-api` (id, content) VALUES(?, ?);");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, content);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
