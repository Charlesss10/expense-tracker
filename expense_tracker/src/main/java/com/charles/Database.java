package com.charles;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

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
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", mysql_username,
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

	// Verify transaction
	public boolean verifyTransaction(TransactionManager transaction) throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String type = transaction.getType();
		int id = transaction.getTransactionId();
		ResultSet incomeSelectQuery, expensesSelectQuery;

		while (true) {
			switch (type) {
				case "INCOME" -> {
					incomeSelectQuery = stmt.executeQuery("select * from income where id = " + id);
					if (incomeSelectQuery.next()) {
						return true;
					} else {
						System.out.println("Invalid ID. Please enter a valid ID: ");
						id = Integer.parseInt(choice.nextLine());
						transaction.setTransactionId(id);
					}
					break;
				}
				case "EXPENSES" -> {
					expensesSelectQuery = stmt.executeQuery("select * from expenses where id = " + id);
					if (expensesSelectQuery.next()) {
						return true;
					} else {
						System.out.println("Invalid ID. Please enter a valid ID: ");
						id = Integer.parseInt(choice.nextLine());
						transaction.setTransactionId(id);
					}
					break;
				}
			}
		}
	}

	// Insert transaction into database
	public void insertTransaction(TransactionManager transaction) throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String type = transaction.getType();
		double amount = transaction.getAmount();
		String source = transaction.getSource();
		String category = transaction.getCategory();
		Date date = transaction.getDate();

		// Validate transaction type argument
		while (!type.equalsIgnoreCase("INCOME") &&
				!type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Wrong type. Enter Income or Expenses: ");
			type = choice.nextLine();
		}
		type = type.toUpperCase();

		switch (type) {
			case "INCOME" -> {
				source = source.toUpperCase();
				stmt.executeUpdate("insert into income(type, amount, source, transaction_date) values ('"
						+ type + "','" + amount + "','" + source
						+ "','" + date + "')");
				break;
			}
			case "EXPENSES" -> {
				category = category.toUpperCase();
				stmt.executeUpdate("insert into expenses(type, amount, category, transaction_date) values ('"
						+ type + "','" + amount + "','" + category
						+ "','" + date + "')");
				break;
			}
		}
	}

	// Update transaction in database
	public void updateTransaction(TransactionManager transaction, String attributeChoice) throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		int id = transaction.getTransactionId();
		String type = transaction.getType();
		double amount = transaction.getAmount();
		String source = transaction.getSource();
		String category = transaction.getCategory();
		Date date = transaction.getDate();

		// Validate transaction type argument
		while (!type.equalsIgnoreCase("INCOME") &&
				!type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Wrong type. Enter Income or Expenses: ");
			type = choice.nextLine();
		}
		type = type.toUpperCase();

		if (verifyTransaction(transaction)) {
			switch (type) {
				case "INCOME" -> {
					// Validate attribute argument for Income
					while (!attributeChoice.equalsIgnoreCase("AMOUNT") &&
							!attributeChoice.equalsIgnoreCase("SOURCE") &&
							!attributeChoice.equalsIgnoreCase("DATE")) {
						System.out.println("Wrong attribute. Enter a valid attribute: Amount, Source or Date: ");
						attributeChoice = choice.nextLine();
					}
					attributeChoice = attributeChoice.toUpperCase();

					switch (attributeChoice) {
						case "AMOUNT" ->
							stmt.executeUpdate("update income set amount = " + amount + " where id = "
									+ id);
						case "SOURCE" -> {
							source = source.toUpperCase();
							stmt.executeUpdate("update income set source = '" + source + "' where id = "
									+ id);
						}
						case "DATE" ->
							stmt.executeUpdate("update income set transaction_date = '" + date + "' where id = "
									+ id);
					}
				}
				case "EXPENSES" -> {
					// Validate attribute argument for Expenses
					while (!attributeChoice.equalsIgnoreCase("AMOUNT") &&
							!attributeChoice.equalsIgnoreCase("CATEGORY") &&
							!attributeChoice.equalsIgnoreCase("DATE")) {
						System.out.println("Wrong attribute. Enter a valid attribute Amount, Category or Date: ");
						attributeChoice = choice.nextLine();
					}
					attributeChoice = attributeChoice.toUpperCase();

					switch (attributeChoice) {
						case "AMOUNT" ->
							stmt.executeUpdate("update expenses set amount = " + amount + " where id = "
									+ id);
						case "CATEGORY" -> {
							category = category.toUpperCase();
							stmt.executeUpdate("update expenses set category = '" + category + "' where id = "
									+ id);
						}
						case "DATE" ->
							stmt.executeUpdate("update expenses set transaction_date = '" + date + "' where id = "
									+ id);
					}
				}
			}
		}
	}

	// Delete transaction in database
	public void removeTransaction(TransactionManager transaction) throws SQLException {
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		int id = transaction.getTransactionId();
		String type = transaction.getType();

		if (verifyTransaction(transaction)) {
			// Validate transaction type argument
			while (!type.equalsIgnoreCase("INCOME") &&
					!type.equalsIgnoreCase("EXPENSES")) {
				System.out.println("Wrong type. Enter Income or Expenses: ");
				type = choice.nextLine();
			}
			type = type.toUpperCase();

			switch (type) {
				case "INCOME" -> {
					stmt.executeUpdate("delete from income where id = " + id);
					break;
				}
				case "EXPENSES" -> {
					stmt.executeUpdate("delete from expenses where id = " + id);
					break;
				}
			}
		}
	}

	// View recent transactions in database
	public void getRecentTransactions() throws SQLException {
		ResultSet incomeSelectQuery, expensesSelectQuery;
		// Income Table
		System.out.println("Income Transactions");
		System.out.println("-------------------");
		incomeSelectQuery = stmt.executeQuery("select * from recentIncome");
		System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
				"ID", "TYPE", "AMOUNT", "SOURCE", "DATE", "ENTRY DATE");
		System.out.println(
				"--------------------------------------------------------------------------------------------------------");

		if (incomeSelectQuery != null) {
			while (incomeSelectQuery.next()) {
				System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
						incomeSelectQuery.getInt(1),
						incomeSelectQuery.getString(2),
						incomeSelectQuery.getDouble(3),
						incomeSelectQuery.getString(4),
						incomeSelectQuery.getString(5),
						incomeSelectQuery.getString(6));
			}
		}

		// Expenses Table
		System.out.println("\nExpenses Transactions");
		System.out.println("---------------------");
		expensesSelectQuery = stmt.executeQuery("select * from recentExpenses");
		System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
				"ID", "TYPE", "AMOUNT", "CATEGORY", "DATE", "ENTRY DATE");
		System.out.println(
				"--------------------------------------------------------------------------------------------------------");

		if (expensesSelectQuery != null) {
			while (expensesSelectQuery.next()) {
				System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
						expensesSelectQuery.getInt(1),
						expensesSelectQuery.getString(2),
						expensesSelectQuery.getDouble(3),
						expensesSelectQuery.getString(4),
						expensesSelectQuery.getString(5),
						expensesSelectQuery.getString(6));
			}
		}
	}

	// View transaction in database
	public void getTransaction(TransactionManager transaction) throws SQLException {
		ResultSet incomeSelectQuery, expensesSelectQuery;
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String type = transaction.getType();
		int id = transaction.getTransactionId();

		// Validate transaction type argument
		while (!type.equalsIgnoreCase("INCOME") &&
				!type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Wrong type. Enter Income or Expenses: ");
			type = choice.nextLine();
		}
		type = type.toUpperCase();

		if (verifyTransaction(transaction)) {
			switch (type) {
				case "INCOME" -> {
					incomeSelectQuery = stmt.executeQuery("select * from income where id = " + id);
					System.out.format("%-5s %-20s %-20s %-20s %-20s%n",
							"ID", "TYPE", "AMOUNT", "SOURCE", "DATE");
					System.out
							.println("-------------------------------------------------------------------------------");

					while (incomeSelectQuery.next()) {
						System.out.format("%-5s %-20s %-20s %-20s %-20s%n",
								incomeSelectQuery.getInt(1),
								incomeSelectQuery.getString(2),
								incomeSelectQuery.getDouble(3),
								incomeSelectQuery.getString(4),
								incomeSelectQuery.getString(5));
					}
					break;
				}
				case "EXPENSES" -> {
					expensesSelectQuery = stmt.executeQuery("select * from expenses where id = " + id);
					System.out.format("%-5s %-20s %-20s %-20s %-20s%n",
							"ID", "TYPE", "AMOUNT", "CATEGORY", "DATE");
					System.out
							.println("-------------------------------------------------------------------------------");

					while (expensesSelectQuery.next()) {
						System.out.format("%-5s %-20s %-20s %-20s %-20s%n",
								expensesSelectQuery.getInt(1),
								expensesSelectQuery.getString(2),
								expensesSelectQuery.getDouble(3),
								expensesSelectQuery.getString(4),
								expensesSelectQuery.getString(5));
					}
					break;
				}
			}
		}
	}

	// Get balance by type
	public double getBalance(String type) throws SQLException {
		ResultSet incomeSelectQuery, expensesSelectQuery;
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		double incomeBalance = 0.0;
		double expensesBalance = 0.0;

		// Validate transaction type argument
		while (!type.equalsIgnoreCase("INCOME") &&
				!type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Wrong type. Enter Income or Expenses: ");
			type = choice.nextLine();
		}
		type = type.toUpperCase();

		switch (type) {
			case "INCOME" -> {
				incomeSelectQuery = stmt.executeQuery("select sum(amount) from income");
				if (incomeSelectQuery.next()) {
					incomeBalance = incomeSelectQuery.getDouble(1);
				}

				return incomeBalance;
			}
			case "EXPENSES" -> {
				expensesSelectQuery = stmt.executeQuery("select sum(amount) from expenses");
				if (expensesSelectQuery.next()) {
					expensesBalance = expensesSelectQuery.getDouble(1);
				}

				return expensesBalance;
			}
			default -> {
				return 0;
			}
		}
	}

	// Filter transaction by date
	public void getFilteredTransactionDate(String type, DateFilter dateFilter) throws SQLException {
		ResultSet incomeSelectQuery, expensesSelectQuery;
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		Date startDate = dateFilter.getStartDate();
		Date endDate = dateFilter.getEndDate();

		// Validate transaction type argument
		while (!type.equalsIgnoreCase("INCOME") &&
				!type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Wrong type. Enter Income or Expenses: ");
			type = choice.nextLine();
		}
		type = type.toUpperCase();

		switch (type) {
			case "INCOME" -> {
				incomeSelectQuery = stmt.executeQuery("select * from income where transaction_date between '"
						+ startDate + "' and '" + endDate + "'");

				System.out.format("\n%-5s %-20s %-20s %-20s %-15s %-15s%n",
						"ID", "TYPE", "AMOUNT", "SOURCE", "DATE", "ENTRY DATE");
				System.out.println(
						"--------------------------------------------------------------------------------------------------------");
				if (incomeSelectQuery != null) {
					while (incomeSelectQuery.next()) {
						System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
								incomeSelectQuery.getInt(1),
								incomeSelectQuery.getString(2),
								incomeSelectQuery.getDouble(3),
								incomeSelectQuery.getString(4),
								incomeSelectQuery.getString(5),
								incomeSelectQuery.getString(6));
					}
				}
				break;
			}

			case "EXPENSES" -> {
				expensesSelectQuery = stmt.executeQuery("select * from expenses where transaction_date between '"
						+ startDate + "' and '" + endDate + "'");
				System.out.format("\n%-5s %-20s %-20s %-20s %-15s %-15s%n",
						"ID", "TYPE", "AMOUNT", "CATEGORY", "DATE", "ENTRY DATE");
				System.out.println(
						"--------------------------------------------------------------------------------------------------------");

				if (expensesSelectQuery != null) {
					while (expensesSelectQuery.next()) {
						System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
								expensesSelectQuery.getInt(1),
								expensesSelectQuery.getString(2),
								expensesSelectQuery.getDouble(3),
								expensesSelectQuery.getString(4),
								expensesSelectQuery.getString(5),
								expensesSelectQuery.getString(6));
					}
				}
				break;
			}
		}
	}

	// Filter transaction by amount
	public void getFilteredTransactionAmount(String type, AmountFilter amountFilter)
			throws SQLException {
		ResultSet incomeSelectQuery, expensesSelectQuery;
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		double startAmount = amountFilter.getStartAmount();
		double endAmount = amountFilter.getEndAmount();

		// Validate transaction type argument
		while (!type.equalsIgnoreCase("INCOME") &&
				!type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Wrong type. Enter Income or Expenses: ");
			type = choice.nextLine();
		}
		type = type.toUpperCase();

		switch (type) {
			case "INCOME" -> {
				incomeSelectQuery = stmt.executeQuery("select * from income where amount between '"
						+ startAmount + "' and '" + endAmount + "'");
				System.out.format("\n%-5s %-20s %-20s %-20s %-15s %-15s%n",
						"ID", "TYPE", "AMOUNT", "SOURCE", "DATE", "ENTRY DATE");
				System.out.println(
						"--------------------------------------------------------------------------------------------------------");
				if (incomeSelectQuery != null) {
					while (incomeSelectQuery.next()) {
						System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
								incomeSelectQuery.getInt(1),
								incomeSelectQuery.getString(2),
								incomeSelectQuery.getDouble(3),
								incomeSelectQuery.getString(4),
								incomeSelectQuery.getString(5),
								incomeSelectQuery.getString(6));
					}
				}
				break;
			}

			case "EXPENSES" -> {
				expensesSelectQuery = stmt.executeQuery("select * from expenses where amount between '"
						+ startAmount + "' and '" + endAmount + "'");
				System.out.format("\n%-5s %-20s %-20s %-20s %-15s %-15s%n",
						"ID", "TYPE", "AMOUNT", "CATEGORY", "DATE", "ENTRY DATE");
				System.out.println(
						"--------------------------------------------------------------------------------------------------------");

				if (expensesSelectQuery != null) {
					while (expensesSelectQuery.next()) {
						System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
								expensesSelectQuery.getInt(1),
								expensesSelectQuery.getString(2),
								expensesSelectQuery.getDouble(3),
								expensesSelectQuery.getString(4),
								expensesSelectQuery.getString(5),
								expensesSelectQuery.getString(6));
					}
				}
				break;
			}
		}
	}

	// Filter transaction by source
	public void getFilteredTransactionSource(String type, SourceFilter sourceFilter)
			throws SQLException {
		ResultSet incomeSelectQuery;
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String source = sourceFilter.getSource();

		// Validate income transaction type arguement
		while (!type.equalsIgnoreCase("INCOME")) {
			System.out.println("Wrong type. Enter Income: ");
			type = choice.nextLine();
		}
		type = type.toUpperCase();
		source = source.toUpperCase();

		switch (type) {
			case "INCOME" -> {
				incomeSelectQuery = stmt.executeQuery("select * from income where source = '"
						+ source + "'");
				System.out.format("\n%-5s %-20s %-20s %-20s %-15s %-15s%n",
						"ID", "TYPE", "AMOUNT", "SOURCE", "DATE", "ENTRY DATE");
				System.out.println(
						"--------------------------------------------------------------------------------------------------------");
				if (incomeSelectQuery != null) {
					while (incomeSelectQuery.next()) {
						System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
								incomeSelectQuery.getInt(1),
								incomeSelectQuery.getString(2),
								incomeSelectQuery.getDouble(3),
								incomeSelectQuery.getString(4),
								incomeSelectQuery.getString(5),
								incomeSelectQuery.getString(6));
					}
				}
				break;
			}
		}

	}

	// Filter transaction by category
	public void getFilteredTransactionCategory(String type, CategoryFilter categoryFilter)
			throws SQLException {
		ResultSet expensesSelectQuery;
		@SuppressWarnings("resource")
		Scanner choice = new Scanner(System.in);
		String category = categoryFilter.getCategory();

		// Validate income transaction type arguement
		while (!type.equalsIgnoreCase("EXPENSES")) {
			System.out.println("Wrong type. Enter Expenses: ");
			type = choice.nextLine();
		}
		type = type.toUpperCase();
		category = category.toUpperCase();

		switch (type) {
			case "EXPENSES" -> {
				expensesSelectQuery = stmt.executeQuery("select * from expenses where category = '"
						+ category + "'");
				System.out.format("\n%-5s %-20s %-20s %-20s %-15s %-15s%n",
						"ID", "TYPE", "AMOUNT", "CATEGORY", "DATE", "ENTRY DATE");
				System.out.println(
						"--------------------------------------------------------------------------------------------------------");
				if (expensesSelectQuery != null) {
					while (expensesSelectQuery.next()) {
						System.out.format("%-5s %-20s %-20s %-20s %-15s %-15s%n",
								expensesSelectQuery.getInt(1),
								expensesSelectQuery.getString(2),
								expensesSelectQuery.getDouble(3),
								expensesSelectQuery.getString(4),
								expensesSelectQuery.getString(5),
								expensesSelectQuery.getString(6));
					}
				}
				break;
			}
		}
	}
}
