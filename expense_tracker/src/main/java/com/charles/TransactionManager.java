package com.charles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionManager {
	private List<Transaction> transactions = new ArrayList<>();
	private final Database database = Database.getInstance();

	public TransactionManager() throws SQLException {
		this.transactions = database.fetchTransactions();
	}

	// Add Transaction
	public void addTransaction(Transaction transaction, int accountId)
			throws ClassNotFoundException, SQLException, IOException {
		// Add Transaction into the database
		switch (transaction.getType()) {
			case "INCOME" -> {
				database.insertTransaction(transaction, accountId);
			}
			case "EXPENSES" -> {
				database.insertTransaction(transaction, accountId);
			}
			default -> {
				System.out.println("Invalid Type: " + transaction.getType());
				return;
			}
		}
		System.out.println("\nTransaction Successful!\n");
	}

	// Edit Transaction
	public boolean editTransaction(Transaction transaction) throws SQLException {
		for (Transaction validTransaction : this.transactions) {
			if (validTransaction.getTransactionId().equals(transaction.getTransactionId())) {
				switch (transaction.getType()) {
					case "INCOME" -> {
						validTransaction.setAmount(transaction.getAmount());
						validTransaction.setSource(transaction.getSource());
						validTransaction.setDescription(transaction.getDescription());
						validTransaction.setDate(transaction.getDate());
					}
					case "EXPENSES" -> {
						validTransaction.setAmount(transaction.getAmount());
						validTransaction.setCategory(transaction.getCategory());
						validTransaction.setDescription(transaction.getDescription());
						validTransaction.setDate(transaction.getDate());
					}
					default -> {
						System.out.println("Invalid Type: " + transaction.getType());
						return false;
					}
				}
				database.updateTransaction(transaction);
				System.out.println("Transaction modified successfully!\n");
				return true;
			}
		}
		System.out.println("Transaction not found");
		return false;
	}

	// Delete Transaction
	public void deleteTransaction(Transaction transaction) throws SQLException {
		for (Transaction validTransaction : this.transactions) {
			if (validTransaction.getTransactionId().equals(transaction.getTransactionId())) {
				switch (transaction.getType()) {
					case "INCOME" -> {
						transactions.remove(validTransaction);
					}
					case "EXPENSES" -> {
						transactions.remove(validTransaction);
					}
					default -> {
						System.out.println("Invalid Type: " + transaction.getType());
						return;
					}
				}
				database.deleteTransaction(transaction);
				System.out.println("Transaction deleted successfully!\n");
				break;
			}
		}
	}

	// Verify transaction from cache
	public boolean verifyTransaction(String transactionId, String type) throws SQLException {

		// Validate transaction type
		if (!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Invalid type: " + type);
			return false;
		}

		return database.verifyTransaction(transactionId, type);
	}

	// Get and view transaction
	public Transaction getTransaction(String transactionId, String type) throws SQLException {
		Transaction transaction;

		if (database.verifyTransaction(transactionId, type)) {
			transaction = database.getTransaction(transactionId, type);
			switch (type) {
				case "INCOME" -> {
					System.out.println("\nINCOME TABLE:");
					System.out.println("-------------");
					System.out.format("%-52s %-20s %-20s %-20s %-20s %-20s%n",
							"TRANSACTION ID", "TYPE", "AMOUNT", "SOURCE", "DESCRIPTION", "DATE");
					System.out
							.println(
									"------------------------------------------------------------------------------------------------------------------");
					System.out.format("%-52s %-20s %-20s %-20s %-20s %-20s%n",
							transaction.getTransactionId(),
							transaction.getType(),
							transaction.getAmount(),
							transaction.getSource(),
							transaction.getDescription(),
							transaction.getDate());
					return transaction;
				}
				case "EXPENSES" -> {
					System.out.println("\nEXPENSES TABLE:");
					System.out.println("---------------");
					System.out.format("%-52s %-20s %-20s %-20s %-20s %-20s%n",
							"TRANSACTION ID", "TYPE", "AMOUNT", "CATEGORY", "DESCRIPTION", "DATE");
					System.out
							.println(
									"------------------------------------------------------------------------------------------------------------------");
					System.out.format("%-52s %-20s %-20s %-20s %-20s %-20s%n",
							transaction.getTransactionId(),
							transaction.getType(),
							transaction.getAmount(),
							transaction.getCategory(),
							transaction.getDescription(),
							transaction.getDate());
					return transaction;
				}
				default -> {
					System.out.println("Invalid Type: " + transaction.getType());
					return null;
				}
			}
		}
		return null;
	}

	// Display total balance
	public void getTotalBalance() throws SQLException {
		double totalBalance;
		double totalIncome = 0.0;
		double totalExpenses = 0.0;

		System.out.println("\nTOTAL BALANCE");
		System.out.println("-------------");

		for (Transaction transaction : this.transactions) {
			switch (transaction.getType()) {
				case "INCOME" -> {
					totalIncome = totalIncome + transaction.getAmount();
					break;
				}
				case "EXPENSES" -> {
					totalExpenses = totalExpenses + transaction.getAmount();
					break;
				}
				default -> System.out.println("Invalid Type.");
			}
		}
		totalBalance = totalIncome - totalExpenses;

		System.out.println("Total Balance: " + totalBalance);
		System.out.println("Total Income: " + totalIncome);
		System.out.println("Total Expenses: " + totalExpenses);
	}

	// Display Recent Trasactions with filtering functionalities
	public void getRecentTransactions(double amountFilterStart, double amountFilterEnd, Date dateFilterStart,
			Date dateFilterEnd,
			String categoryFilter, String sourceFilter) throws SQLException {
				System.out.println(dateFilterStart);
				System.out.println(dateFilterEnd);

			
		List<Transaction> filteredTransactions = this.transactions.stream()
				.filter(t -> (amountFilterStart == 0.0 && amountFilterEnd == 0.0 ||
						(t.getAmount() >= amountFilterStart && t.getAmount() <= amountFilterEnd)) &&

						(dateFilterStart == null && dateFilterEnd == null ||
								t.getDate().compareTo(dateFilterStart) >= 0
										&& t.getDate().compareTo(dateFilterEnd) <= 0)
						&&

						(categoryFilter == null
								|| (t.getType().equals("EXPENSES") && t.getCategory().equalsIgnoreCase(categoryFilter)))
						&&

						(sourceFilter == null
								|| (t.getType().equals("INCOME") && t.getSource().equalsIgnoreCase(sourceFilter))))
				.collect(Collectors.toList());

		if (filteredTransactions.isEmpty()) {
			System.out.println("No transactions found.");
			return;
		}

		boolean incomeHeaderPrinted = false;
		boolean expensesHeaderPrinted = false;

		for (Transaction transaction : filteredTransactions) {
			if (transaction.getType().equals("INCOME")) {
				if (!incomeHeaderPrinted) {
					System.out.println("INCOME TABLE:");
					System.out.println("-------------");
					System.out.format("%-52s %-20s %-20s %-20s %-20s %-20s%n",
							"TRANSACTION ID", "TYPE", "AMOUNT", "SOURCE", "DESCRIPTION", "DATE");
					System.out
							.println(
									"---------------------------------------------------------------------------------------------------------------------------------------------------");
					incomeHeaderPrinted = true;
				}
				System.out.format("%-52s %-20s %-20s %-20s %-20s %-20s%n",
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
					System.out.format("%-52s %-20s %-20s %-20s %-20s %-20s%n",
							"TRANSACTION ID", "TYPE", "AMOUNT", "CATEGORY", "DESCRIPTION", "DATE");
					System.out
							.println(
									"---------------------------------------------------------------------------------------------------------------------------------------------------");
					expensesHeaderPrinted = true;
				}
				System.out.format("%-52s %-20s %-20s %-20s %-20s %-20s%n",
						transaction.getTransactionId(),
						transaction.getType(),
						transaction.getAmount(),
						transaction.getCategory(),
						transaction.getDescription(),
						transaction.getDate());
			}
		}
	}
}