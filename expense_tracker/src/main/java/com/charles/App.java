package com.charles;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
	static UserInterface userInterface;
	static Database database = Database.getInstance();
	static Scanner choice = new Scanner(System.in);
	static boolean login = false;

	// Root entry into the application
	public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException {
		userInterface = new Management();
		String entryChoice;
		System.out.println("A - Login");
		System.out.println("B - Forgot Password");
		System.out.println("C - Create Account");
		entryChoice = choice.nextLine();

		// Validate user input
		while (!entryChoice.equalsIgnoreCase("A") &&
				!entryChoice.equalsIgnoreCase("B") &&
				!entryChoice.equalsIgnoreCase("C")) {
			System.out.println("Wrong option. Retry: ");
			entryChoice = choice.nextLine();
		}
		entryChoice = entryChoice.toUpperCase();

		switch (entryChoice) {
			case "A" -> {
				expenseTracker();
				break;
			}
			case "B" -> {
				boolean passwordReset = userInterface.authManager("RESET");
				if (passwordReset) {
					expenseTracker();
				}
				break;
			}
			case "C" -> {
				boolean accountCreation = userInterface.userAccountManager("ADD");
				if (accountCreation) {
					expenseTracker();
				}
				break;
			}
			default -> System.out.println("Wrong Option!");
		}
	}

	public static void expenseTracker() throws SQLException, ClassNotFoundException, IOException {
		login = userInterface.authManager("LOGIN");
		Management management = (Management) userInterface;
		int failedLoginAttempts = management.getAuthManager().getFailedLoginAttempts();

		if (login == false && failedLoginAttempts == 3) {
			boolean resetPassword = userInterface.authManager("RESET");
			if (resetPassword) {
				expenseTracker();
			}
		} else {
			while (login) {
				// Main Page
				String mainMenuChoice;

				System.out.println("Expense-Tracker");
				System.out.println("---------------");

				System.out.println("A - View Total Balance");
				System.out.println("B - Transaction Manager");
				System.out.println("C - Generate Report Summary");
				System.out.println("D - Data Storage");
				System.out.println("E - Account Manager");
				System.out.println("F - Settings");
				System.out.println("G - Logout");
				mainMenuChoice = choice.nextLine();

				// Validate user input
				while (!mainMenuChoice.equalsIgnoreCase("A") &&
						!mainMenuChoice.equalsIgnoreCase("B") &&
						!mainMenuChoice.equalsIgnoreCase("C") &&
						!mainMenuChoice.equalsIgnoreCase("D") &&
						!mainMenuChoice.equalsIgnoreCase("E") &&
						!mainMenuChoice.equalsIgnoreCase("F") &&
						!mainMenuChoice.equalsIgnoreCase("G")) {
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
						String transactionManagerChoice;

						System.out.println("\nTransaction Manager");
						System.out.println("-------------------");

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
						break;
					}

					case "G" -> {
						login = userInterface.authManager("LOGOUT");
						database.closeConnection();
						System.out.println("You have been logged out.");
						break;
					}

					default -> System.out.println("Invalid Choice.");
				}
			}
		}
	}
}