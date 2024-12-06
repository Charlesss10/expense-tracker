package com.charles;

import java.sql.SQLException;

//Strategy Design Pattern: Interface that defines a common filter function which is implemented by other classes
public interface FilterStrategy {
    public void filter(String type) throws SQLException;
}
