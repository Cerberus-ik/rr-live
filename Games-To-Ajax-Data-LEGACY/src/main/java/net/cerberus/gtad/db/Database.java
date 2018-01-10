package net.cerberus.gtad.db;


import net.cerberus.gtad.io.logger.LogLevel;
import net.cerberus.gtad.io.logger.LogReason;
import net.cerberus.gtad.io.logger.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private String user;
    private String database;
    private String password;
    private int port;
    private String hostname;
    private Connection connection;

    Database(String hostname, int port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    Connection connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
            return connection;
        } catch (SQLException e) {
            Logger.logMessage("An error occurred while connection to the database.", LogLevel.ERROR, LogReason.DB);
            e.printStackTrace();
        }
        return this.connection;
    }

    public boolean checkConnection() {
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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