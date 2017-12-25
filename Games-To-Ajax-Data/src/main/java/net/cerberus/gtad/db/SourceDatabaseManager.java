package net.cerberus.gtad.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import net.cerberus.gtad.common.DatabaseCredentials;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SourceDatabaseManager {

    private MysqlDataSource dataSource;

    public SourceDatabaseManager(DatabaseCredentials databaseCredentials) {
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUser(databaseCredentials.getUser());
        this.dataSource.setPassword(databaseCredentials.getPassword());
        this.dataSource.setServerName(databaseCredentials.getHost());
        this.dataSource.setURL("jdbc:mysql://localhost/" + databaseCredentials.getDatabase() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
    }

    public List<JSONObject> getGames() {
        List<JSONObject> results = new ArrayList<>();
        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT game FROM games;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                results.add(new JSONObject(resultSet.getString(1)));
            }
            preparedStatement.close();
            connection.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

}
