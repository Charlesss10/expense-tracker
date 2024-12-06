package com.charles;

import java.sql.SQLException;
import java.util.Scanner;

//Filter by source
public class SourceFilter implements FilterStrategy {
    private String source;
    private final Database database = Database.getInstance();

    @Override
    public void filter(String type) throws SQLException {
        @SuppressWarnings("resource")
        Scanner choice = new Scanner(System.in);

        System.out.println("Enter Source: ");
        source = choice.nextLine();

        database.getFilteredTransactionSource(type, this);
    }

    public String getSource() {
        return this.source;
    }
}
