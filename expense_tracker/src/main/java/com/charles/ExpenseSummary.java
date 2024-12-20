package com.charles;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseSummary extends TransactionList {
    private double totalExpenses = 0.0;
    private String highestCategory;
    private Map<String, String> expensesPercentage = new HashMap<>();
    private Map<String, String> expensesByCategory = new HashMap<>();

    public ExpenseSummary(AuthManager authManager) throws SQLException {
        super(authManager);
    }

    public void categorizeExpenses() {
        // Group by Category
        if (this.expensesByCategory != null) {
            this.expensesByCategory = new HashMap<>();
        }
        this.expensesByCategory = totalTransactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("EXPENSES")) // Filter only expenses
                .collect(Collectors.groupingBy(
                        Transaction::getCategory, // Group by category
                        Collectors.collectingAndThen(
                                Collectors.summingDouble(t -> t.getAmount()), // Sum amounts
                                String::valueOf // Convert the sum to String
                        )));

        // Highest Category & Percentage
        double highestCategoryAmount = 0.0;
        if (this.expensesPercentage != null) {
            this.expensesPercentage = new HashMap<>();
        }

        for (Map.Entry<String, String> entry : this.expensesByCategory.entrySet()) {
            double currentCategoryAmount = Double.parseDouble(entry.getValue());
            if (currentCategoryAmount > highestCategoryAmount) {
                highestCategoryAmount = currentCategoryAmount;
                this.highestCategory = entry.getKey();
            }
            double percentage = (Double.parseDouble(entry.getValue()) / this.totalExpenses) * 100;
            int wholeNumberPercentage = (int) Math.round(percentage);
            this.expensesPercentage.put(entry.getKey(), String.valueOf(wholeNumberPercentage));
        }
    }

    public void getExpensesSummary() {
        if (this.totalTransactions == null || this.totalTransactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        this.expensesCalculator();
        this.categorizeExpenses();
        System.out.println("Total Expenses: " + this.totalExpenses);
        System.out.println("\nHighest Category: " + this.highestCategory);

        System.out.println("\nExpenses Percentage:");
        System.out.println("--------------------");
        for (Map.Entry<String, String> entry : this.expensesPercentage.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue() + "%");
        }

        System.out.println("\nExpenses by Category:");
        System.out.println("---------------------");
        for (Map.Entry<String, String> entry : this.expensesByCategory.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

    public void expensesCalculator() {
        for (Transaction transaction : this.totalTransactions) {
            if (transaction.getType().equalsIgnoreCase("EXPENSES")) {
                this.totalExpenses = this.totalExpenses + transaction.getAmount();
            }
        }
    }

    @Override
    public void update() {
        try {
            this.totalExpenses = 0.0;
            fetchTransactions();
            System.out.println("Expense Summary: Update in Transaction Manager!\n");
        } catch (SQLException ex) {
        }
    }
}