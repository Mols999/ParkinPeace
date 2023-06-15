import com.example.parkingpeace.db.DB;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginTest {

    @Test
    public void testLoginWithValidCredentials() {



        String testUsername = "CBo";
        String testPassword = "1234";

        boolean result = false;

        try {
            DB db = new DB();
            String username = null;
            String password = null;
            ResultSet rs = db.selectSQLWithResultParams("SELECT fldUsername, fldPassword FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?", testUsername, testPassword);
            if (rs.next()) {
                username = rs.getString("fldUsername");
                password = rs.getString("fldPassword");
                if (username.equals(testUsername) && password.equals(testPassword)) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Assert.assertTrue(result);
    }


    @Test
    public void testLoginWithInvalidCredentials() {
        String testUsername = "Kasper";
        String testPassword = "1234";

        boolean result = false;

        try {
            DB db = new DB();
            String username = null;
            String password = null;
            ResultSet rs = db.selectSQLWithResultParams("SELECT fldUsername, fldPassword FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?", testUsername, testPassword);
            if (rs.next()) {
                username = rs.getString("fldUsername");
                password = rs.getString("fldPassword");
                if (username.equals(testUsername) && password.equals(testPassword)) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Assert.assertFalse(result);
    }

    @Test
    public void testLoginWithEmptyCredentials() {

        String testUsername = "";
        String testPassword = "";

        boolean result = false;

        try {
            DB db = new DB();
            String username = null;
            String password = null;
            ResultSet rs = db.selectSQLWithResultParams("SELECT fldUsername, fldPassword FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?", testUsername, testPassword);
            if (rs.next()) {
                username = rs.getString("fldUsername");
                password = rs.getString("fldPassword");
                if (username.equals(testUsername) && password.equals(testPassword)) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Assert.assertFalse(result);
    }

    @Test
    public void testLoginCaseSensitive() {
        String testUsername = "cBo";
        String testPassword = "1234";

        boolean result = false;

        try {
            DB db = new DB();
            String username = null;
            String password = null;
            ResultSet rs = db.selectSQLWithResultParams("SELECT fldUsername, fldPassword FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?", testUsername, testPassword);
            if (rs.next()) {
                username = rs.getString("fldUsername");
                password = rs.getString("fldPassword");
                if (username.equals(testUsername) && password.equals(testPassword)) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Assert.assertFalse(result);
    }

}
