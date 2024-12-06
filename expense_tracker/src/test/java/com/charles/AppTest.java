package com.charles;

import java.sql.Date;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit test for Expense-Tracker App.
 */
public class AppTest {
    @Test
    public void testAddExpenses() throws SQLException {
        TransactionManager tm = new TransactionManager();
        String date = "2024-10-02";

        tm.setType("Expenses");
        tm.setAmount(4000);
        tm.setCategory("shopping");
        tm.setDate(Date.valueOf(date));

        assertEquals("Expenses", tm.getType(), "Expenses");
        assertEquals(4000, tm.getAmount(), 4000);
        assertEquals("shopping", tm.getCategory(), "shopping");
    }

    @Test
    public void testAddIncome() throws SQLException {
        TransactionManager tm = new TransactionManager();
        String dateString = "2024-10-02";
        Date date = Date.valueOf(dateString);

        tm.setType("Income");
        tm.setAmount(4000);
        tm.setSource("Salary");
        tm.setDate(date);

        assertEquals("Income", tm.getType(), "Income");
        assertEquals(4000, tm.getAmount(), 4000);
        assertEquals("Salary", tm.getSource(), "Salary");
    }


}