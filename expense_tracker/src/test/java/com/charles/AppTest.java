package com.charles;

import java.sql.Date;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Unit test for Expense-Tracker App.
 */
public class AppTest {

    @Test
    public void addUserAccount() throws SQLException {
        UserAccountManager userAccountManager = new UserAccountManager();
        String birthday2String = "2009-12-14";
        Date birthday2 = Date.valueOf(birthday2String);
        String hashedPassword2 = BCrypt.hashpw("joh%14n%3342", BCrypt.gensalt(12));

        UserAccount userAccount2 = new UserAccount("John", "Jane", "john", birthday2, hashedPassword2,
                "john@yahoo.com");

        userAccountManager.addAccount(userAccount2);

        assertTrue(userAccountManager.verifyAccountByUsername("john"));
    }
}