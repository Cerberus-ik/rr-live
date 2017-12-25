package net.cerberus.gtad.common;

public interface DatabaseCredentials {

    int getPort();

    String getHost();

    String getDatabase();

    String getUser();

    String getPassword();
}
