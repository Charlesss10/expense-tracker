package com.charles;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//Singleton database
public class Database {
	private static Database instance;
	private final Connection con;
	private final Statement stmt;

	// Create database connection
	private Database() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String mysql_username = System.getenv("MYSQL_username");
		String mysql_password = System.getenv("MYSQL_password");
		String mysql_hostname = System.getenv("MYSQL_hostname");
		String mysql_schema = System.getenv("MYSQL_schema");
		con = DriverManager.getConnection("jdbc:mysql://" + mysql_hostname + "/" + mysql_schema, mysql_username,
				mysql_password);
		System.out.println("Database Connection created.\n");

		stmt = con.createStatement();
	}

	// Static access point
	@SuppressWarnings("CallToPrintStackTrace")
	public static Database getInstance() {
		if (instance == null) {
			try {
				instance = new Database();
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	// Close database connection
	public void closeConnection() throws SQLException {
		con.close();
		stmt.close();
		instance = null;
		System.out.println("Database Connection closed.\n");
	}

	// Verify transaction from database
	public boolean verifyTransaction(String transactionId, String type) throws SQLException {
		ResultSet incomeSelectQuery, expensesSelectQuery;

		// Validate transaction type
		if (!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Invalid type: " + type);
			return false;
		}
		type = type.toUpperCase();

		switch (type) {
			case "INCOME" -> {
				incomeSelectQuery = stmt
						.executeQuery("select * from income where transactionId = '" + transactionId + "'");
				if (incomeSelectQuery.next()) {
					return true;
				}
				break;
			}
			case "EXPENSES" -> {
				expensesSelectQuery = stmt
						.executeQuery("select * from expenses where transactionId = '" + transactionId + "'");
				if (expensesSelectQuery.next()) {
					return true;
				}
				break;
			}
			default -> {
				System.out.println("Invalid Type: " + type);
				return false;
			}
		}
		System.out.println("Transaction not found.");
		return false;
	}

	// Get transaction in database
	public Transaction getTransaction(String transactionId, String type) throws SQLException {
		ResultSet incomeSelectQuery, expensesSelectQuery;
		Transaction transaction = new Transaction();

		if (verifyTransaction(transactionId, type)) {
			switch (type) {
				case "INCOME" -> {
					incomeSelectQuery = stmt
							.executeQuery("select * from income where transactionId = '" + transactionId + "'");
					while (incomeSelectQuery.next()) {
						transaction.setAccountId(incomeSelectQuery.getInt(1));
						transaction.setTransactionId(incomeSelectQuery.getString(2));
						transaction.setType(incomeSelectQuery.getString(3));
						transaction.setAmount(incomeSelectQuery.getDouble(4));
						transaction.setSource(incomeSelectQuery.getString(5));
						transaction.setDescription(incomeSelectQuery.getString(6));
						transaction.setDate(incomeSelectQuery.getDate(7));
						transaction.setSystem_date(incomeSelectQuery.getTimestamp(8));
					}
					break;
				}
				case "EXPENSES" -> {
					expensesSelectQuery = stmt
							.executeQuery("select * from expenses where transactionId = '" + transactionId + "'");
					while (expensesSelectQuery.next()) {
						transaction.setAccountId(expensesSelectQuery.getInt(1));
						transaction.setTransactionId(expensesSelectQuery.getString(2));
						transaction.setType(expensesSelectQuery.getString(3));
						transaction.setAmount(expensesSelectQuery.getDouble(4));
						transaction.setCategory(expensesSelectQuery.getString(5));
						transaction.setDescription(expensesSelectQuery.getString(6));
						transaction.setDate(expensesSelectQuery.getDate(7));
						transaction.setSystem_date(expensesSelectQuery.getTimestamp(8));
					}
					break;
				}
				default -> {
					System.out.println("Invalid type: " + type);
					return null;
				}
			}
		}
		return transaction;
	}

	// Fetch all transactions
	public List<Transaction> fetchTransactions() throws SQLException {
		ResultSet incomeSelectQuery, expensesSelectQuery;
		List<Transaction> transactions = new ArrayList<>();

		incomeSelectQuery = stmt.executeQuery("select * from income");
		while (incomeSelectQuery.next()) {
			Transaction incomeTransaction = new Transaction();
			incomeTransaction.setAccountId(incomeSelectQuery.getInt(1));
			incomeTransaction.setTransactionId(incomeSelectQuery.getString(2));
			incomeTransaction.setType(incomeSelectQuery.getString(3));
			incomeTransaction.setAmount(incomeSelectQuery.getDouble(4));
			incomeTransaction.setSource(incomeSelectQuery.getString(5));
			incomeTransaction.setDescription(incomeSelectQuery.getString(6));
			incomeTransaction.setDate(incomeSelectQuery.getDate(7));
			incomeTransaction.setSystem_date(incomeSelectQuery.getTimestamp(8));
			transactions.add(incomeTransaction);
		}
		incomeSelectQuery.close();

		expensesSelectQuery = stmt.executeQuery("select * from expenses");
		while (expensesSelectQuery.next()) {
			Transaction expensesTransaction = new Transaction();
			expensesTransaction.setAccountId(expensesSelectQuery.getInt(1));
			expensesTransaction.setTransactionId(expensesSelectQuery.getString(2));
			expensesTransaction.setType(expensesSelectQuery.getString(3));
			expensesTransaction.setAmount(expensesSelectQuery.getDouble(4));
			expensesTransaction.setCategory(expensesSelectQuery.getString(5));
			expensesTransaction.setDescription(expensesSelectQuery.getString(6));
			expensesTransaction.setDate(expensesSelectQuery.getDate(7));
			expensesTransaction.setSystem_date(expensesSelectQuery.getTimestamp(8));
			transactions.add(expensesTransaction);
		}
		expensesSelectQuery.close();

		return transactions;
	}

	// Insert transaction into database
	public void insertTransaction(Transaction transaction, int accountId) throws SQLException {
		String transactionId = transaction.getTransactionId();
		String type = transaction.getType();
		double amount = transaction.getAmount();
		String source = transaction.getSource();
		String category = transaction.getCategory();
		String description = transaction.getDescription();
		Date date = transaction.getDate();

		if (!verifyTransaction(transactionId, type)) {
			switch (type) {
				case "INCOME" -> {
					source = source.toUpperCase();
					description = description.toUpperCase();
					stmt.executeUpdate(
							"insert into income(accountId, transactionId, type, amount, source, description, date) values ("
									+ accountId + ",'" + transactionId + "','" + type + "','" + amount + "','"
									+ source + "','" + description + "','" + date + "')");
					break;
				}
				case "EXPENSES" -> {
					category = category.toUpperCase();
					description = description.toUpperCase();
					stmt.executeUpdate(
							"insert into expenses(accountId, transactionId, type, amount, category, description, date) values ("
									+ accountId + ",'" + transactionId + "','" + type + "','" + amount + "','"
									+ category + "','" + description + "','" + date + "')");
					break;
				}
				default -> {
					System.out.println("Invalid Type: " + transaction.getType());
					return;
				}
			}
		}
	}

	// Update transaction in database
	public void updateTransaction(Transaction transaction) throws SQLException {
		String transactionId = transaction.getTransactionId();
		String type = transaction.getType();
		double amount = transaction.getAmount();
		Date date = transaction.getDate();

		if (verifyTransaction(transactionId, type)) {
			switch (type) {
				case "INCOME" -> {
					String source = transaction.getSource().toUpperCase();
					String description = transaction.getDescription().toUpperCase();
					stmt.executeUpdate(
							"update income set amount = " + amount + " where transactionId = '" + transactionId + "'");
					stmt.executeUpdate("update income set source = '" + source + "' where transactionId = '"
							+ transactionId + "'");
					stmt.executeUpdate("update income set description = '" + description + "' where transactionId = '"
							+ transactionId + "'");
					stmt.executeUpdate(
							"update income set date = '" + date + "' where transactionId = '" + transactionId + "'");
					break;
				}
				case "EXPENSES" -> {
					String category = transaction.getCategory().toUpperCase();
					String description = transaction.getDescription().toUpperCase();
					stmt.executeUpdate("update expenses set amount = " + amount + " where transactionId = '"
							+ transactionId + "'");
					stmt.executeUpdate("update expenses set category = '" + category + "' where transactionId = '"
							+ transactionId + "'");
					stmt.executeUpdate("update income set description = '" + description + "' where transactionId = '"
							+ transactionId + "'");
					stmt.executeUpdate(
							"update expenses set date = '" + date + "' where transactionId ='" + transactionId + "'");
				}
				default -> {
					System.out.println("Invalid Type: " + transaction.getType());
					return;
				}
			}
		}

	}

	// Delete transaction in database
	public void deleteTransaction(Transaction transaction) throws SQLException {
		String transactionId = transaction.getTransactionId();
		String type = transaction.getType();

		if (verifyTransaction(transactionId, type)) {
			switch (type) {
				case "INCOME" -> {
					stmt.executeUpdate("delete from income where transactionId = '" + transactionId + "'");
					break;
				}
				case "EXPENSES" -> {
					stmt.executeUpdate("delete from expenses where transactionId = '" + transactionId + "'");
					break;
				}
				default -> {
					System.out.println("Invalid Type: " + transaction.getType());
					return;
				}
			}
		}
	}

	// Verify user account in database
	public boolean verifyUserAccount(int accountId) throws SQLException {
		ResultSet accountSelectQuery;

		accountSelectQuery = stmt.executeQuery("select * from userAccount where accountId = " + accountId);
		if (accountSelectQuery.next()) {
			System.out.println("User account Id found.\n");
			return true;
		}

		System.out.println("User account Id not found.\n");
		return false;
	}

	// Verify User account by username in database
	public boolean verifyAccountByUsername(String username) throws SQLException {
		ResultSet accountSelectQuery;

		accountSelectQuery = stmt.executeQuery("select * from userAccount where username = '" + username + "'");
		if (accountSelectQuery.next()) {
			System.out.println("User account username found.\n");
			return true;
		}

		System.out.println("User account username not found.\n");
		return false;
	}

	// Get user account in database
	public UserAccount getUserAccount(int accountId) throws SQLException {
		ResultSet accountSelectQuery = null;
		UserAccount userAccount = null;

		if (verifyUserAccount(accountId)) {
			accountSelectQuery = stmt.executeQuery("select * from userAccount where accountId = '" + accountId + "'");
			while (accountSelectQuery.next()) {
				accountId = accountSelectQuery.getInt(1);
				String firstName = accountSelectQuery.getString(2);
				String lastName = accountSelectQuery.getString(3);
				String username = accountSelectQuery.getString(4);
				Date birthday = accountSelectQuery.getDate(5);
				String password = accountSelectQuery.getString(6);
				String email = accountSelectQuery.getString(7);
				userAccount = new UserAccount(firstName, lastName, username, birthday, password, email);
				userAccount.setAccountId(accountId);
			}
		}
		if (accountSelectQuery != null) {
			accountSelectQuery.close();
		}
		return userAccount;
	}

	// Fetch all user accounts
	public List<UserAccount> fetchUserAccounts() throws SQLException {
		ResultSet accountSelectQuery;
		List<UserAccount> userAccounts = new ArrayList<>();

		accountSelectQuery = stmt.executeQuery("select * from userAccount");
		while (accountSelectQuery.next()) {
			int accountId = accountSelectQuery.getInt(1);
			String firstName = accountSelectQuery.getString(2);
			String lastName = accountSelectQuery.getString(3);
			String username = accountSelectQuery.getString(4);
			Date birthday = accountSelectQuery.getDate(5);
			String password = accountSelectQuery.getString(6);
			String email = accountSelectQuery.getString(7);
			UserAccount userAccount = new UserAccount(firstName, lastName, username, birthday, password, email);
			userAccounts.add(userAccount);
			userAccount.setAccountId(accountId);
		}

		accountSelectQuery.close();
		return userAccounts;
	}

	// Insert user account into database
	public void insertAccount(UserAccount userAccount) throws SQLException {
		String firstName = userAccount.getFirstName();
		String lastName = userAccount.getLastName();
		String username = userAccount.getUsername();
		Date birthday = userAccount.getBirthday();
		String password = userAccount.getPassword();
		String email = userAccount.getEmail();

		stmt.executeUpdate(
				"insert into userAccount(firstName, lastName, username, birthday, password, email) values ('"
						+ firstName + "','" + lastName + "','" + username + "','" + birthday
						+ "','"
						+ password + "','" + email + "')");
		System.out.println("Account added Successfully!\n");
	}

	// Update user account in database
	public void updateAccount(UserAccount userAccount) throws SQLException {
		int accountId = userAccount.getAccountId();
		String firstName = userAccount.getFirstName();
		String lastName = userAccount.getLastName();
		String username = userAccount.getUsername();
		Date birthday = userAccount.getBirthday();
		String password = userAccount.getPassword();
		String email = userAccount.getEmail();

		if (verifyUserAccount(accountId)) {
			stmt.executeUpdate(
					"update userAccount set firstName = " + firstName + " where accountId = '" + accountId + "'");
			stmt.executeUpdate(
					"update userAccount set lastName = '" + lastName + "' where accountId = '" + accountId + "'");
			stmt.executeUpdate(
					"update userAccount set username = '" + username + "' where accountId = '" + accountId + "'");
			stmt.executeUpdate(
					"update userAccount set birthday = '" + birthday + "' where accountId = '" + accountId + "'");
			stmt.executeUpdate(
					"update userAccount set password = '" + password + "' where accountId = '" + accountId + "'");
			stmt.executeUpdate("update userAccount set email = '" + email + "' where accountId = '" + accountId + "'");
		}
	}

	// Delete user account in database
	public void deleteUserAccount(UserAccount userAccount) throws SQLException {
		int accountId = userAccount.getAccountId();

		if (verifyUserAccount(accountId)) {
			stmt.executeUpdate("delete from userAccount where accountId = '" + accountId + "'");
		}
	}

	public void updateAccountPassword(int accountId, String password) throws SQLException {
		if (verifyUserAccount(accountId)) {
			stmt.executeUpdate(
					"update userAccount set password = '" + password + "' where accountId = " + accountId);
		}
	}
}