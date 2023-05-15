package com.example.parkingpeace;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

// Server name 51.195.118.225
// Login name sa
// Password dm2022Sommer

public class DB {
    private static Connection con;
    private static PreparedStatement ps;
    private static ResultSet rs;
    private static String port;
    private static String databaseName;
    private static String userName;
    private static String password;

    public static final String NOMOREDATA = "|ND|";
    private static int numberOfColumns;
    private static int currentColumnNumber = 1;

    private static boolean moreData = false;
    private static boolean pendingData = false;
    private static boolean terminated = false;

    private DB() {
    }

    static {
        Properties props = new Properties();
        String fileName = "db.properties";
        InputStream input;
        try {
            input = new FileInputStream(fileName);
            props.load(input);
            port = props.getProperty("port", "1433");
            databaseName = props.getProperty("databaseName");
            userName = props.getProperty("userName", "sa");
            password = props.getProperty("password","1234");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Database Ready");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public class DatabaseConnector {

        // Vi skal kun connecte til den database David har købt. Så vi ikke har 1000 som sidste projekt.
        private static final String CONNECTION_URL = "jdbc:sqlserver://51.195.118.225;databaseName=dbCanteen;user=sa;password=1234";

        public static Connection getConnection() {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(CONNECTION_URL);
            } catch (SQLException e) {
                System.out.println("Error connecting to the database: " + e.getMessage());
            }
            return connection;
        }
    }

    private static void connect() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://LAPTOP-2NQ6KUQ8:"+port+";databaseName="+databaseName, userName, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void disconnect() {
        try {
            con.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void selectSQL(String sql) {
        if (terminated){
            System.exit(0);
        }
        try {
            if (ps!=null){
                ps.close();
            }
            if (rs!=null){
                rs.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            pendingData=true;
            moreData = rs.next();
            ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
        } catch (Exception e){
            System.err.println("Error in the sql parameter, please test this in SQLServer first");
            System.err.println(e.getMessage());
        }
    }

    public static String getDisplayData() {
        if (terminated){
            System.exit(0);
        }
        if (!pendingData){
            terminated=true;
            throw new RuntimeException("ERROR! No previous select, communication with the database is lost!");
        } else if (!moreData){
            disconnect();
            pendingData=false;
            return NOMOREDATA;
        } else {
            return getNextValue(true);
        }
    }

    public static int getNumberOfColumns() {
        return numberOfColumns;
    }

    public static String getData() {
        if (terminated){
            System.exit(0);
        }
        if (!pendingData){
            terminated=true;
            throw new RuntimeException("ERROR! No previous select, communication with the database is lost!");
        } else if (!moreData){
            disconnect();
            pendingData=false;
            return NOMOREDATA;
        } else {
            return getNextValue(false).trim();
        }
    }

    private static String getNextValue(boolean view) {
        StringBuilder value= new StringBuilder();
        try {
            value.append(rs.getString(currentColumnNumber));
            if (currentColumnNumber >= numberOfColumns){
                currentColumnNumber = 1;
                if (view) { value.append("\n");
                }
                moreData = rs.next();
            } else {
                if (view) {
                    value.append(" ");
                }
                currentColumnNumber++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return value.toString();
    }

    public static boolean insertSQL(String sql) {
        return executeUpdate(sql);
    }

    public static boolean updateSQL(String sql) {
        return executeUpdate(sql);
    }

    public static boolean deleteSQL(String sql) {
        return executeUpdate(sql);
    }

    private static boolean executeUpdate(String sql) {
        if (terminated) {
            System.exit(0);
        }
        if (pendingData) {
            terminated = true;
            throw new RuntimeException("ERROR! There were pending data from previous select, communication with the database is lost!");
        }
        try {
            if (ps != null) {
                ps.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            int rows = ps.executeUpdate();
            ps.close();
            if (rows > 0) {
                return true;
            }
        } catch (RuntimeException | SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return false;
    }

}
