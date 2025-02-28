package com.charles;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportSummary extends TransactionList {
    private double totalIncome;
    private double totalExpenses;
    private double totalBalance;
    private String highestCategory;
    private String highestSource;
    private Map<String, String> expensesPercentage = new HashMap<>();
    private Map<String, String> incomePercentage = new HashMap<>();
    private Map<String, String> expensesByCategory = new HashMap<>();
    private Map<String, String> incomeBySource = new HashMap<>();

    public ReportSummary(AuthManager authManager, Settings settings) throws SQLException {
        super(authManager, settings);
    }

    public boolean generateReportSummary(String targetMonth, String targetYear) throws SQLException {
        // Filter transactions based on tragetMonth or targetYear
        List<Transaction> filteredTransactions = this.totalTransactions.stream()
                .filter(t -> {
                    int targetYearValue = 0;
                    int targetMonthValue = 0;

                    if (targetMonth != null) {
                        String[] parts = targetMonth.split("-"); // Split "YYYY-MM" into year and month
                        targetYearValue = Integer.parseInt(parts[0]); // Extract month as integer
                        targetMonthValue = Integer.parseInt(parts[1]); // Extract year as integer
                    } else if (targetYear != null) {
                        targetYearValue = Integer.parseInt(targetYear); // Only year is specified
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(t.getDate()); // Set the Date into the Calendar

                    int transactionMonth = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
                    int transactionYear = cal.get(Calendar.YEAR);

                    boolean matchesMonth = targetMonthValue == 0 || transactionMonth == targetMonthValue;
                    boolean matchesYear = targetYearValue == 0 || transactionYear == targetYearValue;

                    return matchesMonth && matchesYear;
                })
                .collect(Collectors.toList());

        // Generate Report Summary View
        if (!filteredTransactions.isEmpty()) {
            getTotalBalance(filteredTransactions);
            categorizeExpenses(filteredTransactions);
            categorizeIncome(filteredTransactions);

            if (this.highestSource == null) {
                this.highestSource = "N/A";
            }
            if (this.highestCategory == null) {
                this.highestCategory = "N/A";
            }

            System.out.println("Report Summary\n");
            System.out.println("Total Income: " + this.totalIncome + " " + settings.getPreferredCurrency());
            System.out.println("Total Expenses: " + this.totalExpenses + " " + settings.getPreferredCurrency());
            System.out.println("Total Balance: " + this.totalBalance + " " + settings.getPreferredCurrency());
            System.out.println("Highest Source: " + this.highestSource);
            System.out.println("Highest Category: " + this.highestCategory);

            if (!this.incomeBySource.isEmpty()) {
                System.out.println("\nIncome by Source:");
                System.out.println("-----------------");
                for (Map.Entry<String, String> entry : this.incomeBySource.entrySet()) {
                    for (Map.Entry<String, String> percentage : this.incomePercentage.entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(percentage.getKey()))
                            System.out.println(
                                    entry.getKey() + " - " + entry.getValue() + " (" + percentage.getValue() + "%)");
                    }
                }
            }

            if (!this.expensesByCategory.isEmpty()) {
                System.out.println("\nExpenses by Category:");
                System.out.println("---------------------");
                for (Map.Entry<String, String> entry : this.expensesByCategory.entrySet()) {
                    for (Map.Entry<String, String> percentage : this.expensesPercentage.entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(percentage.getKey()))
                            System.out.println(
                                    entry.getKey() + " - " + entry.getValue() + " (" + percentage.getValue() + "%)");
                    }
                }
            }
            return true;
        } else {
            System.out.println("No transaction record found.");
            return false;
        }
    }

    public void getTotalBalance(List<Transaction> filteredTransactions) throws SQLException {
        this.totalIncome = 0.0;
        this.totalExpenses = 0.0;
        this.totalBalance = 0.0;

        for (Transaction transaction : filteredTransactions) {
            switch (transaction.getType()) {
                case "INCOME" -> {
                    this.totalIncome = this.totalIncome + transaction.getAmount();
                    break;
                }
                case "EXPENSES" -> {
                    this.totalExpenses = this.totalExpenses + transaction.getAmount();
                    break;
                }
                default -> System.out.println("Invalid Type.");
            }
        }
        this.totalBalance = this.totalIncome - this.totalExpenses;
    }

    public void categorizeExpenses(List<Transaction> filteredTransactions) {
        // Group by Category
        if (this.expensesByCategory != null) {
            this.expensesByCategory = new HashMap<>();
        }
        this.expensesByCategory = filteredTransactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("EXPENSES")) // Filter only expenses
                .collect(Collectors.groupingBy(
                        Transaction::getCategory, // Group by category
                        Collectors.collectingAndThen(
                                Collectors.summingDouble(t -> t.getAmount()), // Sum amounts
                                String::valueOf // Convert the sum to String
                        )));

        // Highest Category & Percentage
        double highestCategoryAmount = 0.0;
        this.highestCategory = null;
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

    public void categorizeIncome(List<Transaction> filteredTransactions) {
        // Group by Category
        if (this.incomeBySource != null) {
            this.incomeBySource = new HashMap<>();
        }
        this.incomeBySource = filteredTransactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("INCOME")) // Filter only expenses
                .collect(Collectors.groupingBy(
                        Transaction::getSource, // Group by category
                        Collectors.collectingAndThen(
                                Collectors.summingDouble(t -> t.getAmount()), // Sum amounts
                                String::valueOf // Convert the sum to String
                        )));

        // Highest Category & Percentage
        double highestSourceAmount = 0.0;
        this.highestSource = null;
        if (this.incomePercentage != null) {
            this.incomePercentage = new HashMap<>();
        }

        for (Map.Entry<String, String> entry : this.incomeBySource.entrySet()) {
            double currentSourceAmount = Double.parseDouble(entry.getValue());
            if (currentSourceAmount > highestSourceAmount) {
                highestSourceAmount = currentSourceAmount;
                this.highestSource = entry.getKey();
            }
            double percentage = (Double.parseDouble(entry.getValue()) / this.totalIncome) * 100;
            int wholeNumberPercentage = (int) Math.round(percentage);
            this.incomePercentage.put(entry.getKey(), String.valueOf(wholeNumberPercentage));
        }
    }

    public void exportToCSV(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Report Summary\n");
            writer.write(String.format("Total Income: %.2f\n", this.totalIncome));
            writer.write(String.format("Total Expenses: %.2f\n", this.totalExpenses));
            writer.write(String.format("Total Balance: %.2f\n", this.totalBalance));
            writer.write(String.format("Highest Source: %s\n", this.highestSource));
            writer.write(String.format("Highest Category: %s\n\n", this.highestCategory));

            if (!this.incomeBySource.isEmpty()) {
                writer.write("Income by Source:\n");
                writer.write("Source,Amount,Percentage\n");
                this.incomeBySource.forEach((source, amount) -> {
                    try {
                        writer.write(String.format("%s,%s,%s\n", source, amount, this.incomePercentage.get(source)));
                    } catch (IOException e) {
                    }
                });
                writer.write("\n");
            }

            if (!this.expensesByCategory.isEmpty()) {
                writer.write("Expenses by Category:\n");
                writer.write("Category,Amount,Percentage\n");
                this.expensesByCategory.forEach((category, amount) -> {
                    try {
                        writer.write(
                                String.format("%s,%s,%s\n", category, amount, this.expensesPercentage.get(category)));
                    } catch (IOException e) {
                    }
                });
            }
        }
        System.out.println("Report successfully exported to CSV: " + filePath + "\n");
    }

    @Override
    public void update() {
        try {
            fetchTransactions();
            System.out.println("Report Summary: Update in Transaction Manager!\n");
        } catch (SQLException ex) {
        }
    }
}