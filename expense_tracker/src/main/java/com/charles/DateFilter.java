package com.charles;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

//Filter transaction by date
public class DateFilter implements FilterStrategy {
    private Date startDate;
    private Date endDate;
    private final Database database = Database.getInstance();

    @Override
    public void filter(String type) throws SQLException {
        @SuppressWarnings("resource")
        Scanner choice = new Scanner(System.in);

        while (true) {
            System.out.println("Start date(YYYY-MM-DD): ");
            try {
                String dateInput = choice.nextLine();
                startDate = Date.valueOf(dateInput);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid start date.");
            }
        }
        while (true) {
            System.out.println("End date(YYYY-MM-DD): ");
            try {
                String dateInput = choice.nextLine();
                endDate = Date.valueOf(dateInput);
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid end date.");
            }
        }
        if (startDate.compareTo(endDate) > 0) {
            Date correctedDate = startDate;
            startDate = endDate;
            endDate = correctedDate;
        }
        database.getFilteredTransactionDate(type, this);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}