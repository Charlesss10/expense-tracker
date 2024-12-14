package com.charles;

import java.sql.SQLException;

//Filter by amount
public class AmountFilter implements FilterStrategy {
    @Override
    public void filter(double amountFilterStart, double amountFilterEnd, java.util.Date dateFilterStart,
            java.util.Date dateFilterEnd, String categoryFilter, String sourceFilter) throws SQLException {
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.getRecentTransactions(amountFilterStart, amountFilterEnd, dateFilterStart, dateFilterEnd,
                categoryFilter,
                sourceFilter);
    }
}