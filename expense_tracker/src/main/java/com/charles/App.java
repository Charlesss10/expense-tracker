package com.charles;

import java.io.IOException;
import java.sql.SQLException;

public class App {
	private static UserInterface userInterface;

	// Root entry into the application
	public static void main(String args[]) throws SQLException, ClassNotFoundException, IOException{
		UserAccountManager userAccountManager = new UserAccountManager();
		AuthManager authManager = new AuthManager(userAccountManager);
		Settings settings = new Settings(authManager, userAccountManager);
		TransactionManager transactionManager = new TransactionManager(authManager, settings);
		TransactionHistory transactionHistory = new TransactionHistory(authManager, settings);
		ExpenseSummary expenseSummary = new ExpenseSummary(authManager, settings);
		ReportSummary reportSummary = new ReportSummary(authManager, settings);
		DataStorage dataStorage = new DataStorage(transactionManager);

		transactionManager.registerObserver(transactionHistory);
		transactionManager.registerObserver(expenseSummary);
		transactionManager.registerObserver(reportSummary);

		userInterface = new Management(userAccountManager, authManager, transactionManager, transactionHistory, expenseSummary, reportSummary, settings, dataStorage);
		userInterface.run();
	}
}