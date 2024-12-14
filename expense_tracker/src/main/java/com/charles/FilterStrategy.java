package com.charles;

import java.sql.SQLException;
import java.util.Date;

//Strategy Design Pattern: Interface that defines a common filter function which is implemented by other classes
public interface FilterStrategy {
    public void filter(double amountFilterStart, double amountFilterEnd, Date dateFilterStart,
            Date dateFilterEnd,
            String categoryFilter, String sourceFilter) throws SQLException;
}