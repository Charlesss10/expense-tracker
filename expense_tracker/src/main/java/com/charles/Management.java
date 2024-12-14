package com.charles;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

//Handles core functionalities of the software
public class Management implements UserInterface {
	private UserAccountManager userAccountManager;
	private AuthManager authManager;

	public Management() throws SQLException {
		this.userAccountManager = new UserAccountManager();
		this.authManager = new AuthManager(userAccountManager);
	}

	@Override
	public boolean userAccountManager(String userAccountManagerPrompt) throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);

		// Validate user input
		while (!userAccountManagerPrompt.equalsIgnoreCase("ADD") &&
				!userAccountManagerPrompt.equalsIgnoreCase("MODIFY") &&
				!userAccountManagerPrompt.equalsIgnoreCase("DELETE")) {
			System.out.println("Wrong option. Retry: ");
			userAccountManagerPrompt = choice.nextLine();
		}
		userAccountManagerPrompt = userAccountManagerPrompt.toUpperCase();

		switch (userAccountManagerPrompt) {
			// Create User Account
			case "ADD" -> {
				UserAccount userAccount;
				String firstName;
				String lastName;
				String username;
				Date birthday;
				String password;
				String email;

				System.out.println("\nAdd Account");
				System.out.println("-----------");

				System.out.println("First Name: ");
				firstName = choice.nextLine();

				System.out.println("Last Name: ");
				lastName = choice.nextLine();

				System.out.println("Username: ");
				username = choice.nextLine();

				while (true) {
					System.out.println("Birthday(YYYY-MM-DD): ");
					try {
						String birthdayInput = choice.nextLine();
						birthday = Date.valueOf(birthdayInput);
						break;
					} catch (Exception e) {
						System.out.println("Invalid format. Please enter a valid date.");
					}
				}

				do {
					System.out.println("Password: ");
					password = choice.nextLine();
					if (password.length() < 5 || !password.matches(".*[0-9\\W].*")) {
						System.out.println(
								"Invalid password format. The password must be at least 5 characters long, contain both alphanumeric characters and at least one special character.");
					}
				} while (password.length() < 5 || !password.matches(".*[0-9\\W].*"));
				String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

				do {
					System.out.println("Email: ");
					email = choice.nextLine();
					if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
						System.out.println("Invalid email type. Enter a valid email");
					}
				} while (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"));

				userAccount = new UserAccount(firstName, lastName, username, birthday, hashedPassword, email);
				userAccountManager.addAccount(userAccount);
				return true;
			}
			case "MODIFY" -> {
				break;
			}
			case "DELETE" -> {
				break;
			}
			default -> System.out.println("Invalid Prompt.");
		}
		return false;
	}

	@Override
	public boolean authManager(String authManagerPrompt) throws SQLException, IOException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);

		// Validate user input
		while (!authManagerPrompt.equalsIgnoreCase("LOGIN") &&
				!authManagerPrompt.equalsIgnoreCase("LOGOUT") &&
				!authManagerPrompt.equalsIgnoreCase("RESET")) {
			System.out.println("Wrong option. Retry: ");
			authManagerPrompt = choice.nextLine();
		}
		authManagerPrompt = authManagerPrompt.toUpperCase();

		switch (authManagerPrompt) {
			// Login to the Expense Tracker App
			case "LOGIN" -> {
				int loginAttempts = 0;
				String username;
				String password;

				// Load token
				String token = authManager.loadSessionToken();
				if (token != null) {
					String accountId = authManager.validateSessionToken(token);
					if (accountId != null) {
						System.out.println("Welcome back, user " + accountId + "!");
						return true;
					} else {
						System.out.println("Session expired or invalid. Please log in again.");
					}
				}

				System.out.println("Login");
				System.out.println("-----");

				do {
					System.out.println("Username: ");
					username = choice.nextLine();

					System.out.println("Password: ");
					password = choice.nextLine();

					if (authManager.login(username, password)) {
						// Generate and save token if login succeeds
						int accountId = authManager.getAccountId();
						token = authManager.generateSessionToken(accountId);
						authManager.saveSessionToken(token);
						System.out.println("Login successful.");
						return true;
					}
					loginAttempts++;

				} while (loginAttempts < 3);

				authManager.setFailedLoginAttempts(loginAttempts);
				System.out.println("Login failed. Please try again later.");
				break;
			}

			case "LOGOUT" -> {
				return authManager.logout();
			}

			case "RESET" -> {
				String email;
				String password;

				System.out.println("Reset Password");
				System.out.println("--------------");

				do {
					System.out.println("Email: ");
					email = choice.nextLine();
					if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
						System.out.println("Invalid email type. Enter a valid email");
					}
				} while (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"));

				if (authManager.resetPassword(email)) {
					System.out.println("New Password");
					System.out.println("------------");

					do {
						System.out.println("Password: ");
						password = choice.nextLine();
						if (password.length() < 5 || !password.matches(".*[0-9\\W].*")) {
							System.out.println(
									"Invalid password format. The password must be at least 5 characters long, contain both alphanumeric characters and at least one special character.");
						}
					} while (password.length() < 5 || !password.matches(".*[0-9\\W].*"));
					String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

					return authManager.updateAccountPassword(email, hashedPassword);
				}
				break;
			}

			default -> {
				System.out.println("Invalid Prompt.");
				return false;
			}
		}
		return false;
	}

	@Override
	public void transactionManager(String transactionManagerPrompt)
			throws ClassNotFoundException, SQLException, IOException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String continueChoice;
		String transactionChoice;
		TransactionManager transactionManager = new TransactionManager();

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
			// Add a transaction
			case "ADD" -> {
				Transaction transaction = new Transaction();
				int accountId = authManager.getAccountId();
				double amount;
				Date date;

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
							int sourceId;
							System.out.println("\nAdd Income");

							// Set transactionId
							String transactionId = UUID.randomUUID().toString();
							transaction.setTransactionId(transactionId);

							// Set type
							transaction.setType("INCOME");

							// Set amount
							while (true) {
								System.out.println("Transaction amount: ");
								try {
									amount = choice.nextDouble();
									if (amount < 0) {
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
							transaction.setAmount(amount);

							// Set source from drop-down menu
							Map<Integer, String> sourceMap = new HashMap<>();
							sourceMap.put(1, "Salary/Wages");
							sourceMap.put(2, "Business Income");
							sourceMap.put(3, "Freelance/Consulting");
							sourceMap.put(4, "Rental Income");
							sourceMap.put(5, "Investment Income");
							sourceMap.put(6, "Royalties");
							sourceMap.put(7, "Government Benefits");
							sourceMap.put(8, "Inheritance/Gifts");
							sourceMap.put(9, "Other");

							while (true) {
								System.out.println("Transaction source: ");
								for (Map.Entry<Integer, String> entry : sourceMap.entrySet()) {
									System.out.println(entry.getKey() + " - " + entry.getValue());
								}
								try {
									sourceId = choice.nextInt();
									if (sourceId < 0 || sourceId > 9) {
										System.out.println("Invalid input. Please enter a positive value.");
									} else {
										break;
									}
								} catch (Exception e) {
									System.out.println("Invalid input. Please enter a valid option.");
									choice.nextLine();
								}
							}
							choice.nextLine();

							String description;
							if (sourceId == 9) {
								System.out.println("Enter description:");
								choice.nextLine();
								description = choice.nextLine();
							} else {
								description = sourceMap.get(sourceId);
							}
							transaction.setDescription(description);
							transaction.setSource(sourceMap.get(sourceId));

							// Set date
							while (true) {
								System.out.println("Transaction date(YYYY-MM-DD): ");
								try {
									String dateInput = choice.nextLine();
									date = Date.valueOf(dateInput);
									break;
								} catch (Exception e) {
									System.out.println("Invalid input. Please enter a valid date.");
								}
							}
							transaction.setDate(date);
						}

						case "B" -> {
							// Add Expenses
							int categoryId;
							System.out.println("\nAdd Expenses");

							// Set transactionId
							String transactionId = UUID.randomUUID().toString();
							transaction.setTransactionId(transactionId);

							// Set type
							transaction.setType("EXPENSES");

							// Set amount
							while (true) {
								System.out.println("Transaction amount: ");
								try {
									amount = choice.nextDouble();
									if (amount < 0) {
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
							transaction.setAmount(amount);

							// Set category from drop-down menu
							Map<Integer, String> categoryMap = new HashMap<>();
							categoryMap.put(1, "Housing (Rent/Mortgage)");
							categoryMap.put(2, "Utilities (Electricity, Water, Internet)");
							categoryMap.put(3, "Groceries");
							categoryMap.put(4, "Transportation");
							categoryMap.put(5, "Health (Insurance/Medical)");
							categoryMap.put(6, "Education");
							categoryMap.put(7, "Debt Repayment");
							categoryMap.put(8, "Entertainment");
							categoryMap.put(9, "Clothing");
							categoryMap.put(10, "Savings/Investments");
							categoryMap.put(11, "Gifts/Donations");
							categoryMap.put(12, "Other");

							while (true) {
								System.out.println("Transaction category: ");
								for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
									System.out.println(entry.getKey() + " - " + entry.getValue());
								}
								try {
									categoryId = choice.nextInt();
									if (categoryId < 0 || categoryId > 9) {
										System.out.println("Invalid input. Please enter a positive value.");
									} else {
										break;
									}
								} catch (Exception e) {
									System.out.println("Invalid input. Please enter a valid option.");
									choice.nextLine();
								}
							}
							choice.nextLine();

							String description;
							if (categoryId == 12) {
								System.out.println("Enter description:");
								choice.nextLine();
								description = choice.nextLine();
							} else {
								description = categoryMap.get(categoryId);
							}
							transaction.setDescription(description);
							transaction.setCategory(categoryMap.get(categoryId));

							// Set date
							while (true) {
								System.out.println("Transaction date(YYYY-MM-DD): ");
								try {
									String dateInput = choice.nextLine();
									date = Date.valueOf(dateInput);
									break;
								} catch (Exception e) {
									System.out.println("Invalid input. Please enter a valid date.");
								}
							}
							transaction.setDate(date);
						}
					}
					transactionManager.addTransaction(transaction, accountId);
					System.out.println("Would you like to enter another transaction? Y/N");
					continueChoice = choice.nextLine();
				} while (continueChoice.equalsIgnoreCase("Y"));
				break;
			}

			// Modify a transaction
			case "MODIFY" -> {
				String modifyChoice;
				String continueModifyChoice;
				Transaction transaction;
				String type;
				String transactionId;
				double amount;
				Date date;

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

					// Modify Income
					switch (transactionChoice) {
						case "A" -> {
							int sourceId;
							System.out.println("\nModify Income");
							type = "INCOME";

							System.out.println("Enter TransactionId: ");
							transactionId = choice.nextLine();
							transaction = transactionManager.getTransaction(transactionId, type);

							if (transaction == null) {
								break;
							}

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

									// Modify Amount
									case "A" -> {
										while (true) {
											System.out.println("Set amount: ");
											try {
												amount = choice.nextDouble();
												if (amount < 0) {
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
										transaction.setAmount(amount);
										break;
									}

									// Modify Source
									case "B" -> {
										Map<Integer, String> sourceMap = new HashMap<>();
										sourceMap.put(1, "Salary/Wages");
										sourceMap.put(2, "Business Income");
										sourceMap.put(3, "Freelance/Consulting");
										sourceMap.put(4, "Rental Income");
										sourceMap.put(5, "Investment Income");
										sourceMap.put(6, "Royalties");
										sourceMap.put(7, "Government Benefits");
										sourceMap.put(8, "Inheritance/Gifts");
										sourceMap.put(9, "Other");

										while (true) {
											System.out.println("Transaction source: ");
											for (Map.Entry<Integer, String> entry : sourceMap.entrySet()) {
												System.out.println(entry.getKey() + " - " + entry.getValue());
											}
											try {
												sourceId = choice.nextInt();
												if (sourceId < 0 || sourceId > 9) {
													System.out.println("Invalid input. Please enter a positive value.");
												} else {
													break;
												}
											} catch (Exception e) {
												System.out.println("Invalid input. Please enter a valid option.");
												choice.nextLine();
											}
										}
										choice.nextLine();

										String description;
										if (sourceId == 9) {
											System.out.println("Enter description:");
											choice.nextLine();
											description = choice.nextLine();
										} else {
											description = sourceMap.get(sourceId);
										}
										transaction.setDescription(description);
										transaction.setSource(sourceMap.get(sourceId));
										break;
									}

									// Modify Date
									case "C" -> {
										while (true) {
											System.out.println("Set date(YYYY-MM-DD): ");
											try {
												String dateInput = choice.nextLine();
												date = Date.valueOf(dateInput);
												break;
											} catch (Exception e) {
												System.out.println("Invalid input. Please enter a valid date.");
											}
										}
										transaction.setDate(date);
										break;
									}
								}
								boolean result = transactionManager.editTransaction(transaction);
								if (!result) {
									break;
								}
								transactionManager.getTransaction(transactionId, type);
								choice.nextLine();
								System.out.println("Would you like to modify any other attribute Y/N: ");
								continueModifyChoice = choice.nextLine();
							} while (continueModifyChoice.equalsIgnoreCase("Y"));
							break;
						}

						// Modify Expenses
						case "B" -> {
							int categoryId;
							System.out.println("\nModify Expenses");
							type = "EXPENSES";

							System.out.println("Enter TransactionId: ");
							transactionId = choice.nextLine();

							transaction = transactionManager.getTransaction(transactionId, type);
							choice.nextLine();

							if (transaction == null) {
								break;
							}

							do {
								System.out.println("Which attribute would you like to modify: ");
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

									// Modify Amount
									case "A" -> {
										while (true) {
											System.out.println("Set amount: ");
											try {
												amount = choice.nextDouble();
												if (amount < 0) {
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
										transaction.setAmount(amount);
										break;
									}

									// Modify Category
									case "B" -> {
										Map<Integer, String> categoryMap = new HashMap<>();
										categoryMap.put(1, "Housing (Rent/Mortgage)");
										categoryMap.put(2, "Utilities (Electricity, Water, Internet)");
										categoryMap.put(3, "Groceries");
										categoryMap.put(4, "Transportation");
										categoryMap.put(5, "Health (Insurance/Medical)");
										categoryMap.put(6, "Education");
										categoryMap.put(7, "Debt Repayment");
										categoryMap.put(8, "Entertainment");
										categoryMap.put(9, "Clothing");
										categoryMap.put(10, "Savings/Investments");
										categoryMap.put(11, "Gifts/Donations");
										categoryMap.put(12, "Other");

										while (true) {
											System.out.println("Transaction category: ");
											for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
												System.out.println(entry.getKey() + " - " + entry.getValue());
											}
											try {
												categoryId = choice.nextInt();
												if (categoryId < 0 || categoryId > 9) {
													System.out.println("Invalid input. Please enter a positive value.");
												} else {
													break;
												}
											} catch (Exception e) {
												System.out.println("Invalid input. Please enter a valid option.");
												choice.nextLine();
											}
										}
										choice.nextLine();

										String description;
										if (categoryId == 9) {
											System.out.println("Enter description:");
											choice.nextLine();
											description = choice.nextLine();
										} else {
											description = categoryMap.get(categoryId);
										}
										transaction.setDescription(description);
										transaction.setCategory(categoryMap.get(categoryId));
										break;
									}

									// Modify date
									case "C" -> {
										while (true) {
											System.out.println("Set date(YYYY-MM-DD): ");
											try {
												String dateInput = choice.nextLine();
												date = Date.valueOf(dateInput);
												break;
											} catch (Exception e) {
												System.out.println("Invalid input. Please enter a valid date.");
											}
										}
										transaction.setDate(date);
										break;
									}
								}
								transactionManager.editTransaction(transaction);
								if (transactionManager.getTransaction(transactionId, type) == null) {
									break;
								}
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
				break;
			}

			// Delete a transaction
			case "DELETE" -> {
				Transaction transaction;
				String type;
				String transactionId;
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
							type = "INCOME";

							System.out.println("Enter TransactionId: ");
							transactionId = choice.nextLine();

							transaction = transactionManager.getTransaction(transactionId, type);
							choice.nextLine();

							// Deletion Confirmation
							System.out.println("Are you sure that you would like to delete this transaction? Y/N");
							deleteConfirmation = choice.nextLine();

							if (deleteConfirmation.equalsIgnoreCase("Y")) {
								transactionManager.deleteTransaction(transaction);
							}
							break;
						}

						// Delete Expenses
						case "B" -> {
							System.out.println("\nDelete Expenses");
							type = "EXPENSES";

							System.out.println("Enter TransactionId: ");
							transactionId = choice.nextLine();

							transaction = transactionManager.getTransaction(transactionId, type);
							choice.nextLine();

							// Deletion Confirmation
							System.out.println("Are you sure that you would like to delete this transaction? Y/N");
							deleteConfirmation = choice.nextLine();

							if (deleteConfirmation.equalsIgnoreCase("Y")) {
								transactionManager.deleteTransaction(transaction);
							}
							break;
						}
					}
					System.out.println("Would you like to delete another transaction? Y/N");
					continueChoice = choice.nextLine();
				} while (continueChoice.equalsIgnoreCase("Y"));
				break;
			}

			// View total balance
			case "TOTALBALANCE" -> {
				transactionManager.getTotalBalance();
				choice.nextLine();
				break;
			}

			// Display recent transactions with filtering functionalities
			case "RECENTTRANSACTIONS" -> {
				String filterChoice;
				String continueFilterChoice;
				String filterCriteria;
				double amountFilterStart = 0.0;
				double amountFilterEnd = 0.0;
				Date dateFilterStart = Date.valueOf(LocalDate.now().minus(30, ChronoUnit.DAYS));
				Date dateFilterEnd = Date.valueOf(LocalDate.now());
				String categoryFilter = null;
				String sourceFilter = null;

				System.out.println("\nRecent Transactions:\n");

				transactionManager.getRecentTransactions(amountFilterStart, amountFilterEnd,
						dateFilterStart, dateFilterEnd,
						categoryFilter,
						sourceFilter);
				choice.nextLine();

				// Option to filter transaction
				System.out.println("Filter Transaction Y/N");
				filterChoice = choice.nextLine();

				if (!filterChoice.equalsIgnoreCase("Y")) {
					break;
				}

				do {
					// Reset filter criterias
					amountFilterStart = 0.0;
					amountFilterEnd = 0.0;
					dateFilterStart = null;
					dateFilterEnd = null;
					categoryFilter = null;
					sourceFilter = null;

					System.out.println("Choose a filter criteria: ");
					System.out.println("A - Amount");
					System.out.println("B - Source");
					System.out.println("C - Category");
					System.out.println("D - Date");
					filterCriteria = choice.nextLine();

					// Validate user input
					while (!filterCriteria.equalsIgnoreCase("A") &&
							!filterCriteria.equalsIgnoreCase("B") &&
							!filterCriteria.equalsIgnoreCase("C") &&
							!filterCriteria.equalsIgnoreCase("D")) {
						System.out.println("Wrong option. Retry: ");
						filterCriteria = choice.nextLine();
					}
					filterCriteria = filterCriteria.toUpperCase();

					switch (filterCriteria) {
						case "A" -> {
							FilterStrategy filterStrategy = new AmountFilter();
							while (true) {
								System.out.println("Start amount: ");
								try {
									amountFilterStart = choice.nextDouble();
									if (amountFilterStart < 0) {
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
									amountFilterEnd = choice.nextDouble();
									if (amountFilterEnd < 0) {
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

							if (amountFilterEnd < amountFilterStart) {
								double correctedAmount = amountFilterStart;
								amountFilterStart = amountFilterEnd;
								amountFilterEnd = correctedAmount;
							}
							System.out.println("Start Amount" + amountFilterStart);
							System.out.println("End Amount" + amountFilterEnd);

							filterStrategy.filter(amountFilterStart, amountFilterEnd, dateFilterStart,
									dateFilterEnd, categoryFilter, sourceFilter);
							break;
						}

						case "B" -> {
							FilterStrategy filterStrategy = new SourceFilter();
							System.out.println("Enter Source: ");
							sourceFilter = choice.nextLine();
							filterStrategy.filter(amountFilterStart, amountFilterEnd, dateFilterStart,
									dateFilterEnd, categoryFilter, sourceFilter);
							break;
						}

						case "C" -> {
							FilterStrategy filterStrategy = new CategoryFilter();
							System.out.println("Enter Category: ");
							categoryFilter = choice.nextLine();
							filterStrategy.filter(amountFilterStart, amountFilterEnd, dateFilterStart, dateFilterEnd,
									categoryFilter, sourceFilter);
							break;
						}
						case "D" -> {
							FilterStrategy filterStrategy = new DateFilter();
							while (true) {
								System.out.println("Start date(YYYY-MM-DD): ");
								try {
									String dateInput = choice.nextLine();
									dateFilterStart = Date.valueOf(dateInput);
									break;
								} catch (Exception e) {
									System.out.println("Invalid input. Please enter a valid start date.");
								}
							}
							while (true) {
								System.out.println("End date(YYYY-MM-DD): ");
								try {
									String dateInput = choice.nextLine();
									dateFilterEnd = Date.valueOf(dateInput);
									break;
								} catch (Exception e) {
									System.out.println("Invalid input. Please enter a valid end date.");
								}
							}
							if (dateFilterStart != null && dateFilterEnd != null) {
								if (dateFilterStart.compareTo(dateFilterEnd) >= 0) {
									Date temp = dateFilterStart;
									dateFilterStart = dateFilterEnd;
									dateFilterEnd = temp;
								}
								filterStrategy.filter(amountFilterStart, amountFilterEnd, dateFilterStart,
										dateFilterEnd, categoryFilter, sourceFilter);
							}
							break;
						}
					}
					choice.nextLine();
					System.out.println("Would you like to filter based on another criteria Y/N: ");
					continueFilterChoice = choice.nextLine();
				} while (continueFilterChoice.equalsIgnoreCase("Y"));
				break;
			}
			default -> System.out.println("Invalid Prompt.");
		}
	}

	public AuthManager getAuthManager() {
		return this.authManager;
	}

    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    public UserAccountManager getUserAccountManager() {
        return userAccountManager;
    }

    public void setUserAccountManager(UserAccountManager userAccountManager) {
        this.userAccountManager = userAccountManager;
    }
}