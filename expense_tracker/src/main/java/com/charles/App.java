package com.charles;

import java.io.IOException;
import java.sql.SQLException;

public class App {
	private static UserInterface userInterface;

	// Root entry into the application
	public static void main(String args[]) throws SQLException, ClassNotFoundException, IOException{
		UserAccountManager userAccountManager = new UserAccountManager();
		AuthManager authManager = new AuthManager(userAccountManager);
		TransactionManager transactionManager = new TransactionManager(authManager);
		TransactionHistory transactionHistory = new TransactionHistory(authManager);
		ExpenseSummary expenseSummary = new ExpenseSummary(authManager);
		ReportSummary reportSummary = new ReportSummary(authManager);

		transactionManager.registerObserver(transactionHistory);
		transactionManager.registerObserver(expenseSummary);
		transactionManager.registerObserver(reportSummary);

		userInterface = new Management(userAccountManager, authManager, transactionManager, transactionHistory, expenseSummary, reportSummary);
		userInterface.run();
	}
}