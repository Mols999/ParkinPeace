package com.example.parkingpeace;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DB {
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    private String port;
    private String databaseName;
    private String userName;
    private String password;
    public static final String NOMOREDATA = "|ND|";
    private int numberOfColumns;
    private int currentColumnNumber = 1;

    protected boolean moreData = false;
    private boolean pendingData = false;
    private boolean terminated = false;




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

    public void disconnect() {
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



    public boolean updateSQLWithParams(String sql, Object... params) {
        try {
            connect();
            ps = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return false;
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
        try {
            connect();
            ps = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return false;
    }

    public boolean isParkingSpotBooked(String parkingSpotID, LocalDate date) {
        try {
            int parkingSpotIDInt = Integer.parseInt(parkingSpotID);
            String sql = "SELECT COUNT(*) FROM tblBooking WHERE fldParkingSpotID = ? AND DATE(fldStartDateTime) <= ? AND DATE(fldEndDateTime) >= ?";
            connect();
            ps = con.prepareStatement(sql);
            ps.setInt(1, parkingSpotIDInt);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setDate(3, java.sql.Date.valueOf(date));
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return false;
    }

    public List<Booking> fetchBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM tblBooking";

        try {
            connect();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                int bookingID = rs.getInt("fldBookingID");
                int customerID = rs.getInt("fldCustomerID");
                int parkingSpotID = rs.getInt("fldParkingSpotID");
                LocalDateTime startDateTime = rs.getTimestamp("fldStartDateTime").toLocalDateTime();
                LocalDateTime endDateTime = rs.getTimestamp("fldEndDateTime").toLocalDateTime();
                String bookingStatus = rs.getString("fldBookingStatus");

                Booking booking = new Booking(bookingID, customerID, parkingSpotID, startDateTime, endDateTime, bookingStatus);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return bookings;
    }


    public ResultSet selectSQLWithResultParams(String sql, String... params) {
        try {
            connect();
            ps = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }



    public boolean hasMoreData() {
        return moreData;
    }
}
