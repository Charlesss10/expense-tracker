package com.charles;

import java.io.IOException;
import java.sql.SQLException;

//User interface that defines all core functions
public interface UserInterface {
	public void transactionManager(String transactionManagerPrompt)
			throws ClassNotFoundException, SQLException, IOException;
}
