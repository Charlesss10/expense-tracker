package com.charles;

import java.io.IOException;
import java.sql.SQLException;

public interface UserInterface {
	public boolean userAccountManager(String userAccountManagerPrompt) throws SQLException;
	public boolean authManager(String authManagerPrompt) throws SQLException, IOException;
	public void transactionManager(String transactionManagerPrompt) throws ClassNotFoundException, SQLException, IOException;
}