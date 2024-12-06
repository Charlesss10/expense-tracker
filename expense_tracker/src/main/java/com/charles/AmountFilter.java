package com.charles;

import java.sql.SQLException;
import java.util.Scanner;

//Filter by amount
public class AmountFilter implements FilterStrategy {
    private double startAmount;
    private double endAmount;
    private final Database database = Database.getInstance();

    @Override
    public void filter(String type) throws SQLException {
        @SuppressWarnings("resource")
        Scanner choice = new Scanner(System.in);

        while (true) {
            System.out.println("Start amount: ");
            try {
                startAmount = choice.nextDouble();
                if (startAmount < 0) {
                    System.out.println("Invalid input. Please enter a positive value.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid start amount.");
                choice.nextLine();
            }
        }
        choice.nextLine();

        while (true) {
            System.out.println("End amount: ");
            try {
                endAmount = choice.nextDouble();
                if (endAmount < 0) {
                    System.out.println("Invalid input. Please enter a positive value.");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid end amount.");
                choice.nextLine();
            }
        }
        choice.nextLine();

        if (endAmount < startAmount) {
            double correctedAmount = startAmount;
            startAmount = endAmount;
            endAmount = correctedAmount;
        }
        database.getFilteredTransactionAmount(type, this);
    }

    public double getStartAmount() {
        return startAmount;
    }

    public double getEndAmount() {
        return endAmount;
    }

    public void setStartAmount(double startAmount) {
        this.startAmount = startAmount;
    }

    public void setEndAmount(double endAmount) {
        this.endAmount = endAmount;
    }
}
