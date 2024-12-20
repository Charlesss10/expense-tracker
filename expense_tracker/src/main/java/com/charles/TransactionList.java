package com.charles;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class TransactionList implements Observer {
    protected List<Transaction> totalTransactions = new ArrayList<>();
    protected final Database database = Database.getInstance();
    protected AuthManager authManager;

    public TransactionList(AuthManager authManager) throws SQLException {
        this.authManager = authManager;
    }

    public void fetchTransactions() throws SQLException {
        this.totalTransactions = database.fetchTransactions(this.authManager.getAccountId());
    }
}