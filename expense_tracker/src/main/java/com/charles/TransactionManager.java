package com.charles;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class TransactionManager {
	private String type;
	private double amount;
	private String category;
	private String source;
	private Date date;
	private double totalIncome;
	private double totalExpenses;
	private int transactionId;
	private final Database database = Database.getInstance();

	public String getType() {
		return this.type;
	}

	public double getAmount() {
		return this.amount;
	}

	public String getCategory() {
		return this.category;
	}

	public String getSource() {
		return this.source;
	}

	public Date getDate() {
		return this.date;
	}

	public double getTotalIncome() {
		return this.totalIncome;
	}

	public double getTotalExpenses() {
		return this.totalExpenses;
	}

	public int getTransactionId() {
		return this.transactionId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setTransactionId(int id) {
		this.transactionId = id;
	}

	// Add Transaction
	public void addTransaction() throws ClassNotFoundException, SQLException, IOException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String transactionChoice;
		String continueChoice;

		do {
			System.out.println("\nAdd Transaction");
			System.out.println("A. Income");
			System.out.println("B. Expenses");
			transactionChoice = choice.nextLine();

			// Validate user input
			while (!transactionChoice.equalsIgnoreCase("A") &&
					!transactionChoice.equalsIgnoreCase("B")) {
				System.out.println("Wrong option. Retry: ");
				transactionChoice = choice.nextLine();
			}
			transactionChoice = transactionChoice.toUpperCase();

			switch (transactionChoice) {

				case "A" -> {
					// Add Income
					System.out.println("\nAdd Income");
					this.type = "INCOME";

					while (true) {
						System.out.println("Transaction amount: ");
						try {
							this.amount = choice.nextDouble();
							if (this.amount < 0) {
								System.out.println("Invalid input. Please enter a positive value.");
							} else {
								break;
							}
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid number.");
							choice.nextLine(); // Clear the invalid input
						}
					}
					choice.nextLine();

					System.out.println("Transaction source: ");
					this.source = choice.nextLine();

					while (true) {
						System.out.println("Transaction date(YYYY-MM-DD): ");
						try {
							String dateInput = choice.nextLine();
							this.date = Date.valueOf(dateInput);
							break;
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid date.");
						}
					}
				}

				case "B" -> {
					// Add Expenses
					System.out.println("\nAdd Expenses");
					this.type = "EXPENSES";

					while (true) {
						System.out.println("Transaction amount: ");
						try {
							this.amount = choice.nextDouble();
							if (this.amount < 0) {
								System.out.println("Invalid input. Please enter a positive value.");
							} else {
								break;
							}
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid number.");
							choice.nextLine();
						}
					}
					choice.nextLine();

					System.out.println("Transaction category: ");
					this.category = choice.nextLine();

					while (true) {
						System.out.println("Transaction date(YYYY-MM-DD): ");
						try {
							String dateInput = choice.nextLine();
							this.date = Date.valueOf(dateInput);
							break;
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid date.");
						}
					}
				}
			}

			database.insertTransaction(this);
			System.out.println("\nTransaction Successful!\n");

			System.out.println("Would you like to enter another transaction? Y/N");
			continueChoice = choice.nextLine();

		} while (continueChoice.equalsIgnoreCase("Y"));
	}

	// Edit Transaction
	public void editTransaction() throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String transactionChoice;
		String continueChoice;
		String modifyChoice;
		String continueModifyChoice;

		do {
			System.out.println("\nModify Transaction");
			System.out.println("A. Income");
			System.out.println("B. Expenses");
			transactionChoice = choice.nextLine();

			// Validate user input
			while (!transactionChoice.equalsIgnoreCase("A") &&
					!transactionChoice.equalsIgnoreCase("B")) {
				System.out.println("Wrong option. Retry: ");
				transactionChoice = choice.nextLine();
			}
			transactionChoice = transactionChoice.toUpperCase();

			// Edit Income
			switch (transactionChoice) {
				case "A" -> {
					System.out.println("\nModify Income");
					this.type = "INCOME";

					while (true) {
						System.out.println("Enter TransactionId: ");
						try {
							this.transactionId = choice.nextInt();
							break;
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid number.");
							choice.nextLine();
						}
					}
					choice.nextLine();

					database.verifyTransaction(this);

					database.getTransaction(this);
					choice.nextLine();

					do {
						System.out.println("Which attribute would you like to modify: ");
						System.out.println("A. Amount");
						System.out.println("B. Source");
						System.out.println("C. Date");
						modifyChoice = choice.nextLine();

						// Validate user input
						while (!modifyChoice.equalsIgnoreCase("A") &&
								!modifyChoice.equalsIgnoreCase("B") &&
								!modifyChoice.equalsIgnoreCase("C")) {
							System.out.println("Wrong option. Retry: ");
							modifyChoice = choice.nextLine();
						}
						modifyChoice = modifyChoice.toUpperCase();

						switch (modifyChoice) {
							case "A" -> {
								while (true) {
									System.out.println("Set amount: ");
									try {
										this.amount = choice.nextDouble();
										if (this.amount < 0) {
											System.out.println("Invalid input. Please enter a positive value.");
										} else {
											break;
										}
									} catch (Exception e) {
										System.out.println("Invalid input. Please enter a valid number.");
										choice.nextLine();
									}
								}
								choice.nextLine();

								database.updateTransaction(this, "AMOUNT");
								break;
							}
							case "B" -> {
								System.out.println("Set Source: ");
								this.source = choice.nextLine();
								database.updateTransaction(this, "SOURCE");
								break;
							}
							case "C" -> {
								while (true) {
									System.out.println("Set date(YYYY-MM-DD): ");
									try {
										String dateInput = choice.nextLine();
										this.date = Date.valueOf(dateInput);
										break;
									} catch (Exception e) {
										System.out.println("Invalid input. Please enter a valid date.");
									}
								}
								database.updateTransaction(this, "DATE");
								break;
							}
						}
						System.out.println("Transaction modified successfully!\n");
						database.getTransaction(this);
						choice.nextLine();
						System.out.println("\nWould you like to modify any other attribute Y/N: ");
						continueModifyChoice = choice.nextLine();
					} while (continueModifyChoice.equalsIgnoreCase("Y"));
					break;
				}

				// Edit Expenses
				case "B" -> {
					System.out.println("\nModify Expenses");
					this.type = "EXPENSES";

					while (true) {
						System.out.println("Enter TransactionId: ");
						try {
							this.transactionId = choice.nextInt();
							break;
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid number.");
							choice.nextLine();
						}
					}
					choice.nextLine();

					database.verifyTransaction(this);

					database.getTransaction(this);
					choice.nextLine();

					do {
						System.out.println("\nWhich attribute would you like to modify: ");
						System.out.println("A. Amount");
						System.out.println("B. Category");
						System.out.println("C. Date");
						modifyChoice = choice.nextLine();

						// Validate user input
						while (!modifyChoice.equalsIgnoreCase("A") &&
								!modifyChoice.equalsIgnoreCase("B") &&
								!modifyChoice.equalsIgnoreCase("C")) {
							System.out.println("Wrong option. Retry: ");
							modifyChoice = choice.nextLine();
						}
						modifyChoice = modifyChoice.toUpperCase();

						switch (modifyChoice) {
							case "A" -> {
								while (true) {
									System.out.println("Set amount: ");
									try {
										this.amount = choice.nextDouble();
										if (this.amount < 0) {
											System.out.println("Invalid input. Please enter a positive value.");
										} else {
											break;
										}
									} catch (Exception e) {
										System.out.println("Invalid input. Please enter a valid number.");
										choice.nextLine();
									}
								}
								choice.nextLine();

								database.updateTransaction(this, "AMOUNT");
								break;
							}
							case "B" -> {
								System.out.println("Set Category: ");
								this.category = choice.nextLine();
								database.updateTransaction(this, "CATEGORY");
								break;
							}
							case "C" -> {
								while (true) {
									System.out.println("Set date(YYYY-MM-DD): ");
									try {
										String dateInput = choice.nextLine();
										this.date = Date.valueOf(dateInput);
										break;
									} catch (Exception e) {
										System.out.println("Invalid input. Please enter a valid date.");
									}
								}
								database.updateTransaction(this, "DATE");
								break;
							}
						}
						System.out.println("Transaction modified successfully!\n");
						database.getTransaction(this);
						choice.nextLine();
						System.out.println("\nWould you like to modify any other attribute Y/N: ");
						continueModifyChoice = choice.nextLine();
					} while (continueModifyChoice.equalsIgnoreCase("Y"));
					break;
				}
			}
			System.out.println("Would you like to modify another transaction? Y/N");
			continueChoice = choice.nextLine();
		} while (continueChoice.equalsIgnoreCase("Y"));
	}

	// Delete Transaction
	public void deleteTransaction() throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String transactionChoice;
		String continueChoice;
		String deleteConfirmation;

		do {
			System.out.println("\nDelete Transaction");
			System.out.println("A. Income");
			System.out.println("B. Expenses");
			transactionChoice = choice.nextLine();

			// Validate user input
			while (!transactionChoice.equalsIgnoreCase("A") &&
					!transactionChoice.equalsIgnoreCase("B")) {
				System.out.println("Wrong option. Retry: ");
				transactionChoice = choice.nextLine();
			}
			transactionChoice = transactionChoice.toUpperCase();

			switch (transactionChoice) {
				// Delete Income
				case "A" -> {
					System.out.println("\nDelete Income");
					this.type = "INCOME";

					while (true) {
						System.out.println("Enter TransactionId: ");
						try {
							this.transactionId = choice.nextInt();
							break;
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid number.");
							choice.nextLine();
						}
					}

					choice.nextLine();

					database.verifyTransaction(this);

					database.getTransaction(this);
					choice.nextLine();

					// Deletion Confirmation
					System.out.println("Are you sure that you would like to delete this transaction? Y/N");
					deleteConfirmation = choice.nextLine();

					if (deleteConfirmation.equalsIgnoreCase("Y")) {
						database.removeTransaction(this);
						System.out.println("Transaction deleted successfully!\n");
					}
					break;
				}

				// Delete Expenses
				case "B" -> {
					System.out.println("\nDelete Expenses");
					this.type = "EXPENSES";

					while (true) {
						System.out.println("Enter TransactionId: ");
						try {
							this.transactionId = choice.nextInt();
							break;
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid number.");
							choice.nextLine();
						}
					}

					database.verifyTransaction(this);

					database.getTransaction(this);
					choice.nextLine();

					// Deletion Confirmation
					System.out.println("Are you sure that you would like to delete this transaction? Y/N");
					deleteConfirmation = choice.nextLine();

					if (deleteConfirmation.equalsIgnoreCase("Y")) {
						database.removeTransaction(this);
						System.out.println("Transaction deleted successfully!\n");
					}
					break;
				}
			}
			System.out.println("Would you like to delete another transaction? Y/N");
			continueChoice = choice.nextLine();
		} while (continueChoice.equalsIgnoreCase("Y"));
	}

	// Display total balance
	public void getTotalBalance() throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		double totalBalance;

		this.totalIncome = database.getBalance("INCOME");
		this.totalExpenses = database.getBalance("EXPENSES");

		totalBalance = this.totalIncome - this.totalExpenses;

		System.out.println("\nTotal Balance: " + totalBalance);
		System.out.println("Total Income: " + this.totalIncome);
		System.out.println("Total Expenses: " + this.totalExpenses);
		choice.nextLine();
	}

	// Display Recent Trasactions with filtering functionalities
	public void getRecentTransactions() throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String filterCriteria;
		String transactionChoice;
		String continueFilterChoice;
		String continueChoice;
		FilterStrategy filterStrategy;

		do {
			System.out.println("\nRecent Transactions:\n");
			database.getRecentTransactions();
			choice.nextLine();

			// Option to filter transaction
			System.out.println("A - Filter Income");
			System.out.println("B - Filter Expenses");
			transactionChoice = choice.nextLine();

			if (!transactionChoice.equalsIgnoreCase("A") &&
					!transactionChoice.equalsIgnoreCase("B")) {
				break;
			}

			transactionChoice = transactionChoice.toUpperCase();

			switch (transactionChoice) {
				case "A" -> {
					this.type = "INCOME";
					do {
						System.out.println("Choose a filter criteria: ");
						System.out.println("A - Amount");
						System.out.println("B - Source");
						System.out.println("C - Date");
						filterCriteria = choice.nextLine();

						// Validate user input
						while (!filterCriteria.equalsIgnoreCase("A") &&
								!filterCriteria.equalsIgnoreCase("B") &&
								!filterCriteria.equalsIgnoreCase("C")) {
							System.out.println("Wrong option. Retry: ");
							filterCriteria = choice.nextLine();
						}
						filterCriteria = filterCriteria.toUpperCase();

						switch (filterCriteria) {
							case "A" -> {
								filterStrategy = new AmountFilter();
								filterStrategy.filter(this.getType());
								break;
							}
							case "B" -> {
								filterStrategy = new SourceFilter();
								filterStrategy.filter(this.getType());
								break;
							}
							case "C" -> {
								filterStrategy = new DateFilter();
								filterStrategy.filter(this.getType());
								break;
							}
						}
						choice.nextLine();
						System.out.println("Would you like to filter based on another criteria Y/N: ");
						continueFilterChoice = choice.nextLine();
					} while (continueFilterChoice.equalsIgnoreCase("Y"));
					break;
				}
				case "B" -> {
					this.type = "EXPENSES";
					do {
						System.out.println("Choose a filter criteria: ");
						System.out.println("A - Amount");
						System.out.println("B - Category");
						System.out.println("C - Date");
						filterCriteria = choice.nextLine();

						// Validate user input
						while (!filterCriteria.equalsIgnoreCase("A") &&
								!filterCriteria.equalsIgnoreCase("B") &&
								!filterCriteria.equalsIgnoreCase("C")) {
							System.out.println("Wrong option. Retry: ");
							filterCriteria = choice.nextLine();
						}
						filterCriteria = filterCriteria.toUpperCase();

						switch (filterCriteria) {
							case "A" -> {
								filterStrategy = new AmountFilter();
								filterStrategy.filter(this.getType());
								break;
							}
							case "B" -> {
								filterStrategy = new CategoryFilter();
								filterStrategy.filter(this.getType());
								break;
							}
							case "C" -> {
								filterStrategy = new DateFilter();
								filterStrategy.filter(this.getType());
								break;
							}
						}
						choice.nextLine();
						System.out.println("Would you like to filter based on another criteria Y/N: ");
						continueFilterChoice = choice.nextLine();
					} while (continueFilterChoice.equalsIgnoreCase("Y"));
					break;
				}
			}
			System.out.println("Would you like to filter another transaction? Y/N");
			continueChoice = choice.nextLine();
		} while (continueChoice.equalsIgnoreCase("Y"));
	}
}