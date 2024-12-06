package com.charles;

import java.sql.SQLException;
import java.util.Scanner;

//Filter by category
public class CategoryFilter implements FilterStrategy {
    private String category;
    private final Database database = Database.getInstance();

    @Override
    public void filter(String type) throws SQLException {
        @SuppressWarnings("resource")
        Scanner choice = new Scanner(System.in);

        System.out.println("Enter Category: ");
        category = choice.nextLine();

        database.getFilteredTransactionCategory(type, this);
    }

    String getCategory() {
        return this.category;
    }

}
