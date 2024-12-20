package com.charles;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

// Handles core functionalities of the software
public class Management implements UserInterface {
	private final UserAccountManager userAccountManager;
	private final AuthManager authManager;
	private final TransactionManager transactionManager;
	private final TransactionHistory transactionHistory;
	private final ExpenseSummary expenseSummary;
	private final ReportSummary reportSummary;
	Scanner choice = new Scanner(System.in);

	public Management(UserAccountManager userAccountManager, AuthManager authManager,
			TransactionManager transactionManager, TransactionHistory transactionHistory, ExpenseSummary expenseSummary,
			ReportSummary reportSummary)
			throws SQLException {
		this.userAccountManager = userAccountManager;
		this.authManager = authManager;
		this.transactionManager = transactionManager;
		this.transactionHistory = transactionHistory;
		this.expenseSummary = expenseSummary;
		this.reportSummary = reportSummary;
	}

	@Override
	public boolean userAccountManager(String userAccountManagerPrompt) throws SQLException {
		UserAccount userAccount;
		String firstName;
		String lastName;
		String username;
		Date birthday = null;
		String password;
		String email = null;

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
				String modifyChoice;
				String continueModifyChoice;
				String hashedPassword;

				int userAccountId = authManager.getAccountId();
				userAccount = userAccountManager.getUserAccount(userAccountId);

				System.out.println("\nModify Account");
				System.out.println("--------------");

				do {
					System.out.println("Which attribute would you like to modify: ");
					System.out.println("A. First Name");
					System.out.println("B. Last Name");
					System.out.println("C. Username");
					System.out.println("D. Birthday");
					System.out.println("E. Password");
					System.out.println("F. Email");

					modifyChoice = choice.nextLine();

					// Validate user input
					while (!modifyChoice.equalsIgnoreCase("A") &&
							!modifyChoice.equalsIgnoreCase("B") &&
							!modifyChoice.equalsIgnoreCase("C") &&
							!modifyChoice.equalsIgnoreCase("D") &&
							!modifyChoice.equalsIgnoreCase("E") &&
							!modifyChoice.equalsIgnoreCase("F")) {
						System.out.println("Wrong option. Retry: ");
						modifyChoice = choice.nextLine();
					}
					modifyChoice = modifyChoice.toUpperCase();

					switch (modifyChoice) {
						case "A" -> {
							System.out.println("First Name: ");
							firstName = choice.nextLine();
							userAccount.setFirstName(firstName);
							break;
						}
						case "B" -> {
							System.out.println("Last Name: ");
							lastName = choice.nextLine();
							userAccount.setLastName(lastName);
							break;
						}
						case "C" -> {
							System.out.println("Username: ");
							username = choice.nextLine();
							userAccount.setUsername(username);
							break;
						}
						case "D" -> {
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
							userAccount.setBirthday(birthday);
							break;
						}
						case "E" -> {
							do {
								System.out.println("Password: ");
								password = choice.nextLine();
								if (password.length() < 5 || !password.matches(".*[0-9\\W].*")) {
									System.out.println(
											"Invalid password format. The password must be at least 5 characters long, contain both alphanumeric characters and at least one special character.");
								}
							} while (password.length() < 5 || !password.matches(".*[0-9\\W].*"));
							hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
							userAccount.setPassword(hashedPassword);
							try {
								authManager.terminateSession();
							} catch (IOException ex) {
							}
							break;
						}
						case "F" -> {
							do {
								System.out.println("Email: ");
								email = choice.nextLine();
								if (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
									System.out.println("Invalid email type. Enter a valid email");
								}
							} while (!email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"));
							userAccount.setEmail(email);
							break;
						}
					}
					userAccountManager.editUserAccount(userAccount);
					System.out.println("Would you like to modify any other attribute Y/N: ");
					continueModifyChoice = choice.nextLine();
				} while (continueModifyChoice.equalsIgnoreCase("Y"));
				return true;
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
						authManager.setAccountId(Integer.parseInt(accountId));
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
						// Set sessionId
						String sessionId = UUID.randomUUID().toString();
						authManager.saveSession(token, sessionId, accountId);
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
		transactionManager.fetchTransactions();
		expenseSummary.fetchTransactions();
		transactionHistory.fetchTransactions();
		String continueChoice;
		String transactionChoice;

		// Income sources
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

		// Expense Categories
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

		// Validate user input
		while (!transactionManagerPrompt.equalsIgnoreCase("ADD") &&
				!transactionManagerPrompt.equalsIgnoreCase("MODIFY") &&
				!transactionManagerPrompt.equalsIgnoreCase("DELETE") &&
				!transactionManagerPrompt.equalsIgnoreCase("TOTALBALANCE") &&
				!transactionManagerPrompt.equalsIgnoreCase("RECENTTRANSACTIONS") &&
				!transactionManagerPrompt.equalsIgnoreCase("EXPENSESUMMARY") &&
				!transactionManagerPrompt.equalsIgnoreCase("HISTORY")) {
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
								System.out.println("Enter description (Press any key to skip):");
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
							while (true) {
								System.out.println("Transaction category: ");
								for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
									System.out.println(entry.getKey() + " - " + entry.getValue());
								}
								try {
									categoryId = choice.nextInt();
									if (categoryId < 0 || categoryId > 12) {
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
								System.out.println("Enter description  (Press any key to skip):");
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
										while (true) {
											System.out.println("Transaction category: ");
											for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
												System.out.println(entry.getKey() + " - " + entry.getValue());
											}
											try {
												categoryId = choice.nextInt();
												if (categoryId < 0 || categoryId > 12) {
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

							if (transaction == null) {
								break;
							}

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

							if (transaction == null) {
								break;
							}

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
									dateFilterEnd, categoryFilter, sourceFilter, transactionManager);
							break;
						}

						case "B" -> {
							FilterStrategy filterStrategy = new SourceFilter();
							System.out.println("Enter Source: ");
							sourceFilter = choice.nextLine();
							filterStrategy.filter(amountFilterStart, amountFilterEnd, dateFilterStart,
									dateFilterEnd, categoryFilter, sourceFilter, transactionManager);
							break;
						}

						case "C" -> {
							FilterStrategy filterStrategy = new CategoryFilter();
							System.out.println("Enter Category: ");
							categoryFilter = choice.nextLine();
							filterStrategy.filter(amountFilterStart, amountFilterEnd, dateFilterStart, dateFilterEnd,
									categoryFilter, sourceFilter, transactionManager);
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
										dateFilterEnd, categoryFilter, sourceFilter, transactionManager);
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

			case "EXPENSESUMMARY" -> {
				System.out.println("\nExpense Summary:");
				System.out.println("----------------\n");

				expenseSummary.getExpensesSummary();
				choice.nextLine();
				break;
			}

			case "HISTORY" -> {
				System.out.println("\nTransaction History: \n");
				transactionHistory.getTransactionHistory();
				choice.nextLine();
				break;
			}
			default -> System.out.println("Invalid Prompt.");
		}
	}

	public void reportSummary() throws SQLException, IOException {
		reportSummary.fetchTransactions();
		String reportType;
		String continueReportChoice;
		String exportChoice;

		System.out.println("\nReport Summary:");
		System.out.println("---------------\n");

		do {
			System.out.println("Choose a report type: ");
			System.out.println("A - Monthly");
			System.out.println("B - Yearly");
			System.out.println("C - General");
			reportType = choice.nextLine();

			// Validate user input
			while (!reportType.equalsIgnoreCase("A") &&
					!reportType.equalsIgnoreCase("B") &&
					!reportType.equalsIgnoreCase("C")) {
				System.out.println("Wrong option. Retry: ");
				reportType = choice.nextLine();
			}
			reportType = reportType.toUpperCase();

			switch (reportType) {
				case "A" -> {
					System.out.println("Enter the target month (YYYY-MM): ");
					String targetMonth = null;
					while (true) {
						try {
							targetMonth = choice.nextLine();
							YearMonth.parse(targetMonth);
							break;
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a month.");
						}
					}

					ReportStrategy reportStrategy = new MonthlyReport();
					boolean reportResult = reportStrategy.generateReport(targetMonth, null, this.reportSummary);
					choice.nextLine();
					if (reportResult) {
						System.out.println("Would you like to export to .csv Y/N: ");
						exportChoice = choice.nextLine();

						if (exportChoice.equalsIgnoreCase("Y")) {
							this.reportSummary.exportToCSV(targetMonth + "_report_summary.csv");
						}
					}
					break;
				}
				case "B" -> {
					System.out.println("Enter the target year (YYYY): ");
					String targetYear = null;
					while (true) {
						try {
							targetYear = choice.nextLine();
							Year.parse(targetYear);
							break;
						} catch (Exception e) {
							System.out.println("Invalid input. Please enter a valid year.");
						}
					}

					ReportStrategy reportStrategy = new YearlyReport();
					boolean reportResult = reportStrategy.generateReport(null, targetYear, this.reportSummary);
					choice.nextLine();
					if (reportResult) {
						System.out.println("Would you like to export to .csv Y/N: ");
						exportChoice = choice.nextLine();

						if (exportChoice.equalsIgnoreCase("Y")) {
							this.reportSummary.exportToCSV(targetYear + "_report_summary.csv");
						}
					}
					break;
				}
				case "C" -> {
					boolean reportResult = reportSummary.generateReportSummary(null, null);
					choice.nextLine();
					if (reportResult) {
						System.out.println("Would you like to export to .csv Y/N: ");
						exportChoice = choice.nextLine();

						if (exportChoice.equalsIgnoreCase("Y")) {
							this.reportSummary.exportToCSV("general_report_summary.csv");
						}
					}
					break;
				}
				default -> {
					System.out.println("Invalid report type");
				}
			}

			System.out.println("Would you like to generate another report Y/N: ");
			continueReportChoice = choice.nextLine();
		} while (continueReportChoice.equalsIgnoreCase("Y"));
	}

	@Override
	public void run() throws ClassNotFoundException, SQLException, IOException {
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
				boolean passwordReset = authManager("RESET");
				if (passwordReset) {
					expenseTracker();
				}
				break;
			}
			case "C" -> {
				boolean accountCreation = userAccountManager("ADD");
				if (accountCreation) {
					expenseTracker();
				}
				break;
			}
			default -> System.out.println("Wrong Option!");
		}
	}

	public void expenseTracker() throws SQLException, ClassNotFoundException, IOException {
		boolean login = authManager("LOGIN");
		int failedLoginAttempts = authManager.getFailedLoginAttempts();

		if (login == false && failedLoginAttempts == 3) {
			boolean resetPassword = authManager("RESET");
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
						transactionManager("TOTALBALANCE");
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
								transactionManager("RECENTTRANSACTIONS");
								break;
							}

							case "B" -> {
								transactionManager("EXPENSESUMMARY");
								break;
							}

							case "C" -> {
								transactionManager("ADD");
								break;
							}

							case "D" -> {
								transactionManager("MODIFY");
								break;
							}

							case "E" -> {
								transactionManager("DELETE");
								break;
							}

							case "F" -> {
								transactionManager("HISTORY");
								break;
							}
						}

						break;
					}

					case "C" -> {
						reportSummary();
						break;
					}

					case "D" -> {
						break;
					}

					case "E" -> {
						userAccountManager("MODIFY");
						break;
					}

					case "F" -> {
						break;
					}

					case "G" -> {
						login = authManager("LOGOUT");
						System.out.println("You have been logged out.");
						break;
					}

					default -> System.out.println("Invalid Choice.");
				}
			}
		}
	}
}