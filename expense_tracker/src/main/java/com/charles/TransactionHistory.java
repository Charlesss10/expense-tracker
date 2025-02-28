package com.charles;

import java.sql.SQLException;

public class TransactionHistory extends TransactionList {
    public TransactionHistory(AuthManager authManager, Settings settings) throws SQLException {
        super(authManager, settings);
    }

    public void getTransactionHistory() {
        if (this.totalTransactions == null || this.totalTransactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        boolean incomeHeaderPrinted = false;
        boolean expensesHeaderPrinted = false;

        for (Transaction transaction : this.totalTransactions) {
            if (transaction.getType().equals("INCOME")) {
                if (!incomeHeaderPrinted) {
                    System.out.println("INCOME TABLE:");
                    System.out.println("-------------");
                    System.out.format("%-40s %-12s %-31s %-33s %-50s %-20s%n",
                            "TRANSACTION ID", "TYPE", "AMOUNT (" + settings.getPreferredCurrency() + ")", "SOURCE", "DESCRIPTION", "DATE");
                    System.out
                            .println(
                                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    incomeHeaderPrinted = true;
                }
                System.out.format("%-40s %-12s %-31s %-33s %-50s %-20s%n",
                        transaction.getTransactionId(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getSource(),
                        transaction.getDescription(),
                        transaction.getDate());

            } else if (transaction.getType().equals("EXPENSES")) {
                if (!expensesHeaderPrinted) {
                    System.out.println("\nEXPENSES TABLE:");
                    System.out.println("--------------");
                    System.out.format("%-40s %-12s %-31s %-43s %-50s %-20s%n",
                            "TRANSACTION ID", "TYPE", "AMOUNT (" + settings.getPreferredCurrency() + ")", "CATEGORY", "DESCRIPTION", "DATE");
                    System.out
                            .println(
                                    "-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    expensesHeaderPrinted = true;
                }
                System.out.format("%-40s %-12s %-31s %-43s %-50s %-20s%n",
                        transaction.getTransactionId(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getCategory(),
                        transaction.getDescription(),
                        transaction.getDate());
            }
        }
    }

    @Override
    public void update() {
        try {
            fetchTransactions();
            System.out.println("Transaction History: Update in Transaction Manager!\n");
        } catch (SQLException ex) {
        }
    }
}