package com.example.parkingpeace.db;

import com.example.parkingpeace.models.Booking;
import com.example.parkingpeace.models.Review;

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
            int spotID = Integer.parseInt(parkingSpotID);
            String sql = "SELECT COUNT(*) FROM tblBooking WHERE fldParkingSpotID = ? AND DATE(fldStartDateTime) <= ? AND DATE(fldEndDateTime) >= ?";
            connect();
            ps = con.prepareStatement(sql);
            ps.setInt(1, spotID);
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

    public boolean isParkingSpotBooked(int parkingSpotID, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM tblBooking WHERE fldParkingSpotID = ? AND fldStartDateTime < ? AND fldEndDateTime > ?";

        try {
            connect();
            ps = con.prepareStatement(sql);
            ps.setInt(1, parkingSpotID);
            ps.setTimestamp(2, Timestamp.valueOf(date.plusDays(1).atStartOfDay())); // Start of next day
            ps.setTimestamp(3, Timestamp.valueOf(date.atStartOfDay())); // Start of this day
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
    public boolean deleteBooking(int bookingID) {
        String sql = "DELETE FROM tblBooking WHERE fldBookingID = ?";

        try {
            connect();
            ps = con.prepareStatement(sql);
            ps.setInt(1, bookingID);

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return false;
    }


    public String getCustomerName(int customerID) {
        String sql = "SELECT fldCustomerName FROM tblCustomer WHERE fldCustomerID = ?";

        try {
            connect();
            ps = con.prepareStatement(sql);
            ps.setInt(1, customerID);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("fldCustomerName");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }

        return "";
    }




    public List<Review> fetchLandlordReviews(int landlordID) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM tblRating WHERE fldLandlordID = ?";

        try {
            connect();
            ps = con.prepareStatement(sql);
            ps.setInt(1, landlordID);
            rs = ps.executeQuery();

            while (rs.next()) {
                int ratingID = rs.getInt("fldRatingID");
                int customerID = rs.getInt("fldCustomerID");
                int ratingValue = rs.getInt("fldRatingValue");
                String ratingComment = rs.getString("fldRatingComment");

                Review review = new Review(ratingID, customerID,ratingValue, ratingComment);
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return reviews;
    }

    public List<Review> fetchCustomerReviews(String customerIDParam) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM tblRatingCustomer WHERE fldCustomerID = ?";

        try {
            connect();
            ps = con.prepareStatement(sql);

            if (customerIDParam != null) {
                ps.setInt(1, Integer.parseInt(customerIDParam));
            } else {
                // Handle the case when customerIDParam is null
                ps.setNull(1, Types.INTEGER);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                int ratingID = rs.getInt("fldRatingID");
                int customerID = rs.getInt("fldCustomerID");
                int ratingValue = rs.getInt("fldRatingValue");
                String ratingComment = rs.getString("fldRatingComment");

                Review review = new Review(ratingID, customerID, ratingValue, ratingComment);
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return reviews;
    }



    public List<LocalDate> getBookedDatesForParkingSpot(String parkingSpotID) {
        List<LocalDate> bookedDates = new ArrayList<>();
        String sql = "SELECT fldStartDateTime, fldEndDateTime FROM tblBooking WHERE fldParkingSpotID = ?";

        try {
            connect();
            ps = con.prepareStatement(sql);
            ps.setString(1, parkingSpotID);
            rs = ps.executeQuery();

            while (rs.next()) {
                LocalDateTime startDateTime = rs.getTimestamp("fldStartDateTime").toLocalDateTime();
                LocalDateTime endDateTime = rs.getTimestamp("fldEndDateTime").toLocalDateTime();

                LocalDate date = startDateTime.toLocalDate();
                while (!date.isAfter(endDateTime.toLocalDate())) {
                    bookedDates.add(date);
                    date = date.plusDays(1);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }

        return bookedDates;

    }


    public int getLandlordIDFromParkingSpot(int parkingSpotID) {
        String sql = "SELECT fldLandlordID FROM tblParkingSpot WHERE fldParkingSpotID = ?";

        try {
            connect();
            ps = con.prepareStatement(sql);
            ps.setInt(1, parkingSpotID);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("fldLandlordID");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }

        return 0;
    }


    public boolean addReview(int landlordID, int ratingValue, String ratingComment) {
        String sql = "INSERT INTO tblRatingLandlord (fldLandlordID, fldRatingValue, fldRatingComment) VALUES (?, ?, ?)";

        try {
            connect();
            ps = con.prepareStatement(sql);
            ps.setInt(1, landlordID);
            ps.setInt(2, ratingValue);
            ps.setString(3, ratingComment);

            int rows = ps.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            disconnect();
        }
        return false;
    }
}
