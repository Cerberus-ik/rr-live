package net.cerberus.gtad.db;


import net.cerberus.gtad.common.DatabaseCredentials;
import net.cerberus.gtad.io.logs.LogLevel;
import net.cerberus.gtad.io.logs.LogReason;
import net.cerberus.gtad.io.logs.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private Connection connection;
    private DatabaseCredentials databaseCredentials;

    Database(DatabaseCredentials databaseCredentials) {
        this.databaseCredentials = databaseCredentials;
    }

    void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.databaseCredentials.getHost() + ":" + this.databaseCredentials.getPort() + "/" + this.databaseCredentials.getDatabase(), this.databaseCredentials.getUser(), this.databaseCredentials.getPassword());
        } catch (SQLException e) {
            Logger.logMessage("An error occurred while connection to the database.", LogLevel.ERROR, LogReason.DB);
            e.printStackTrace();
        }
    }

    Connection getConnection() {
        return this.connection;
    }

    void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection = null;
    }
}