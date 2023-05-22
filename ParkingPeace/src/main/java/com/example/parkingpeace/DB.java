package com.example.parkingpeace;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

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

    public DB() {
        initialize();
    }

    private void initialize() {
        Properties props = new Properties();
        String fileName = "db.properties";
        InputStream input;
        try {
            input = getClass().getClassLoader().getResourceAsStream(fileName);
            props.load(input);
            port = props.getProperty("port", "1433");
            databaseName = props.getProperty("databaseName", "dbParkinPeaceTOPG");
            userName = props.getProperty("userName", "sa");
            password = props.getProperty("password", "dm2022Sommer");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Database Ready");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void connect() {
        try {
            String url = "jdbc:sqlserver://51.195.118.225:" + port + ";databaseName=" + databaseName + ";encrypt=false;trustServerCertificate=true;sslProtocol=TLSv1.2";
            con = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    public void close() {
        disconnect();
    }
    private void disconnect() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void selectSQL(String sql) {
        if (terminated) {
            System.exit(0);
        }
        try {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            pendingData = true;
            moreData = rs.next();
            ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
        } catch (Exception e) {
            System.err.println("Error in the sql parameter, please test this in SQLServer first");
            System.err.println(e.getMessage());
        }
    }

    public String getDisplayData() {
        if (terminated) {
            System.exit(0);
        }
        if (!pendingData) {
            terminated = true;
            throw new RuntimeException("ERROR! No previous select, communication with the database is lost!");
        } else if (!moreData) {
            disconnect();
            pendingData = false;
            return NOMOREDATA;
        } else {
            return getNextValue(true);
        }
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public String getData() {
        if (terminated) {
            System.exit(0);
        }
        if (!pendingData) {
            terminated = true;
            throw new RuntimeException("ERROR! No previous select, communication with the database is lost!");
        } else if (!moreData) {
            disconnect();
            pendingData = false;
            return NOMOREDATA;
        } else {
            return getNextValue(false).trim();
        }
    }

    private String getNextValue(boolean view) {
        StringBuilder value = new StringBuilder();
        try {
            value.append(rs.getString(currentColumnNumber));
            if (currentColumnNumber >= numberOfColumns) {
                currentColumnNumber = 1;
                if (view) {
                    value.append("\n");
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

    public boolean insertSQL(String sql, Object... values) {
        return executeUpdate(sql, values);
    }

    public boolean updateSQL(String sql, Object... values) {
        return executeUpdate(sql, values);
    }

    public boolean deleteSQL(String sql, Object... values) {
        return executeUpdate(sql, values);
    }

    private boolean executeUpdate(String sql, Object... values) {
        if (terminated) {
            System.exit(0);
        }
        if (pendingData) {
            terminated = true;
            throw new RuntimeException("ERROR! There were pending data from the previous select, communication with the database is lost!");
        }
        try {
            if (ps != null) {
                ps.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
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
    public boolean selectSQLWithParams(String sql, String... params) {
        if (terminated) {
            System.exit(0);
        }
        try {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            connect();
            ps = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            pendingData = true;
            moreData = rs.next();
            if (!moreData) {
                disconnect();
                pendingData = false;
            }
            ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
            return moreData;
        } catch (Exception e) {
            System.err.println("Error in the sql parameter, please test this in SQL Server first");
            System.err.println(e.getMessage());
            disconnect();
            pendingData = false;
            return false;
        }
    }




}
