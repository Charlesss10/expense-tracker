package com.charles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    static UserInterface userInterface = new Management();

	//Root entry into the application
	public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String mainMenuChoice;
		String transactionManagerChoice;
		Database database = Database.getInstance();
		boolean logoutOption = true;

		do {
			// Main Page
			System.out.println("Expense-Tracker");
			System.out.println("---------------\n");

			System.out.println("A - View Total Balance");
			System.out.println("B - Transaction Manager");
			System.out.println("C - Generate Report Summary");
			System.out.println("D - Data Storage");
			System.out.println("E - Settings");
			System.out.println("F - Logout");
			mainMenuChoice = choice.nextLine();

			// Validate user input
			while (!mainMenuChoice.equalsIgnoreCase("A") &&
					!mainMenuChoice.equalsIgnoreCase("B") &&
					!mainMenuChoice.equalsIgnoreCase("C") &&
					!mainMenuChoice.equalsIgnoreCase("D") &&
					!mainMenuChoice.equalsIgnoreCase("E") &&
					!mainMenuChoice.equalsIgnoreCase("F")) {
				System.out.println("Wrong option. Retry: ");
				mainMenuChoice = choice.nextLine();
			}
			mainMenuChoice = mainMenuChoice.toUpperCase();

			switch (mainMenuChoice) {
				case "A" -> {
					userInterface.transactionManager("TOTALBALANCE");
					break;
				}

				// Transaction Manager Page
				case "B" -> {
					System.out.println("\nTransaction Manager");
					System.out.println("-------------------\n");

					System.out.println("A - View Recent Transactions");
					System.out.println("B - View Expense Summary");
					System.out.println("C - Add Transaction");
					System.out.println("D - Edit Transaction");
					System.out.println("E - Delete Transaction");
					System.out.println("F - View Transaction History");
					transactionManagerChoice = choice.nextLine();

					// Validate user input
					while (!transactionManagerChoice.equalsIgnoreCase("A") &&
							!transactionManagerChoice.equalsIgnoreCase("B") &&
							!transactionManagerChoice.equalsIgnoreCase("C") &&
							!transactionManagerChoice.equalsIgnoreCase("D") &&
							!transactionManagerChoice.equalsIgnoreCase("E") &&
							!transactionManagerChoice.equalsIgnoreCase("F")) {
						System.out.println("Wrong option. Retry: ");
						transactionManagerChoice = choice.nextLine();
					}
					transactionManagerChoice = transactionManagerChoice.toUpperCase();

					switch (transactionManagerChoice) {
						case "A" -> {
							userInterface.transactionManager("RECENTTRANSACTIONS");
							break;
						}

						case "B" -> {
							break;
						}

						case "C" -> {
							userInterface.transactionManager("ADD");
							break;
						}

						case "D" -> {
							userInterface.transactionManager("MODIFY");
							break;
						}

						case "E" -> {
							userInterface.transactionManager("DELETE");
							break;
						}

						case "F" -> {
							break;
						}
					}

					break;
				}

				case "C" -> {
					break;
				}

				case "D" -> {
					break;
				}

				case "E" -> {
					break;
				}

				case "F" -> {
					logoutOption = false;
					System.out.println("You have been logged out.");
					database.closeConnection();
					break;
				}
			}
		} while (logoutOption);
	}
}

