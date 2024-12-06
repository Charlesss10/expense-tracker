package com.charles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

//Handles core functionalities of the software
public class Management implements UserInterface {
	TransactionManager transactionManager = new TransactionManager();

	@Override
	public void transactionManager(String transactionManagerPrompt)
			throws ClassNotFoundException, SQLException, IOException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);

		// Validate user input
		while (!transactionManagerPrompt.equalsIgnoreCase("ADD") &&
				!transactionManagerPrompt.equalsIgnoreCase("MODIFY") &&
				!transactionManagerPrompt.equalsIgnoreCase("DELETE") &&
				!transactionManagerPrompt.equalsIgnoreCase("TOTALBALANCE") &&
				!transactionManagerPrompt.equalsIgnoreCase("RECENTTRANSACTIONS")) {
			System.out.println("Wrong option. Retry: ");
			transactionManagerPrompt = choice.nextLine();
		}
		transactionManagerPrompt = transactionManagerPrompt.toUpperCase();

		switch (transactionManagerPrompt) {
			case "ADD" -> {
				// Add a transaction
				transactionManager.addTransaction();
				break;
			}
			case "MODIFY" -> {
				// Modify a transaction
				transactionManager.editTransaction();
				break;
			}
			case "DELETE" -> {
				// Modify a transaction
				transactionManager.deleteTransaction();
				break;
			}
			case "TOTALBALANCE" -> {
				// Display total balance
				transactionManager.getTotalBalance();
				break;
			}
			case "RECENTTRANSACTIONS" -> {
				// Display recent transactions with filtering functionalities
				transactionManager.getRecentTransactions();
				break;
			}
		}

	}
}
