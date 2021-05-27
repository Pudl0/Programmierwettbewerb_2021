package de.pcvikings.discordbot.manager;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

public class DatabaseManager {
    public static Object Callback;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private Connection con;

    public DatabaseManager(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        connect();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?sslMode=DISABLED", username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return con != null;
    }

    public void reconnect() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connect();
    }

    public Connection getConnection() {
        return con;
    }

    public ResultSet doSync(String query) {
        HashMap<String, ResultSet> result = new HashMap<>();

        try {
            Statement executeStatement = con.createStatement();

            ResultSet rs = executeStatement.executeQuery(query);

            result.put("ResultSet", rs);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result.get("ResultSet");
    }

    public void doAsync(boolean isUpdate, String query, List<Object> values, Callback<HashMap> callback) {
        Thread executionThread = new Thread(() -> {
            HashMap<String, ResultSet> result = new HashMap<>();

            try {
                PreparedStatement statement = con.prepareStatement(query);
                for(int i = 0; i < values.size(); i++) {
                    statement.setObject(i+1, values.get(i));
                }

                if(isUpdate) {
                    statement.executeUpdate();
                } else {
                    ResultSet rs = statement.executeQuery();

                    result.put("ResultSet", rs);
                }

                callback.onSuccess(result);
            } catch(SQLException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });

        executionThread.setDaemon(true);
        executionThread.start();
    }

    public interface Callback<T> {
        public void onSuccess(T done) throws SQLException;
        public void onFailure(Throwable cause);
    }
}
