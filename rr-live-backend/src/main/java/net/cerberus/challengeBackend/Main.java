package net.cerberus.challengeBackend;

import net.cerberus.challengeBackend.api.API;
import net.cerberus.challengeBackend.db.DatabaseCredentials;
import net.cerberus.challengeBackend.db.DatabaseManager;
import net.cerberus.challengeBackend.io.logs.LogLevel;
import net.cerberus.challengeBackend.io.logs.LogReason;
import net.cerberus.challengeBackend.io.logs.Logger;

public class Main {

    private static DatabaseManager databaseManager;

    public static void main(String[] args) {
        Logger.logMessage("Starting up...", LogLevel.INFO, LogReason.API);
        databaseManager = new DatabaseManager(new DatabaseCredentials() {
            @Override
            public int getPort() {
                return 3306;
            }

            @Override
            public String getHost() {
                return System.getenv("DB_URL");
            }

            @Override
            public String getDatabase() {
                return System.getenv("DB_DB");
            }

            @Override
            public String getUser() {
                return System.getenv("DB_USER");
            }

            @Override
            public String getPassword() {
                return System.getenv("DB_PW");
            }
        });
        API api = new API();
        api.initialize();
        api.startV1();
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
