package net.cerberus.gg.db;


public class DatabaseManagerLegacy {

//    private Connection connection;
//
//    public DatabaseManagerLegacy(String host, String db, String user, String pw) {
//        Logger.logMessage("Connecting to db.", LogLevel.INFO, LogReason.DB);
//        this.connect();
//    }
//
//    private void connect(){
//        this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3006/" + db + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", user, pw);
//        System.out.println(connection.isClosed());
//    }
//
//    private void closeConnection() {
//        try {
//            this.connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void cacheGame(long id, Region region, JSONObject game) {
//        try {
//            this.database.connect();
//            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("INSERT INTO games (gameId, region, game, timestamp) VALUE (?, ?, ?, ?)");
//            preparedStatement.setLong(1, id);
//            preparedStatement.setString(2, region.getPlatformId());
//            preparedStatement.setString(3, game.toString());
//            preparedStatement.setLong(4, game.getJSONObject("game").getLong("timestamp"));
//            preparedStatement.executeUpdate();
//            preparedStatement.close();
//            this.database.closeConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean gameAlreadyCached(long id, Region region) {
//        PreparedStatement preparedStatement = null;
//        try {
//            this.database.connect();
//            preparedStatement = this.database.getConnection().prepareStatement("SELECT gameId FROM games WHERE gameId = ? AND region = ?");
//            preparedStatement.setLong(1, id);
//            preparedStatement.setString(2, region.getPlatformId());
//            ResultSet results = preparedStatement.executeQuery();
//            if (results.next()) {
//                this.closeConnection();
//                preparedStatement.close();
//                return true;
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            this.database.closeConnection();
//            preparedStatement.close();
//        }
//        return false;
//    }

}
