package net.cerberus.challengeBackend.db;

public interface DatabaseCredentials {

    int getPort();

    String getHost();

    String getDatabase();

    String getUser();

    String getPassword();
}
