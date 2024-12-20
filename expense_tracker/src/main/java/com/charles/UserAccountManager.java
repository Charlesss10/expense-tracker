package com.charles;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserAccountManager {
    private List<UserAccount> userAccounts = new ArrayList<>();
    private final Database database = Database.getInstance();

    public UserAccountManager() throws SQLException {
        this.userAccounts = database.fetchUserAccounts();
    }

    // Add User Account
    public void addAccount(UserAccount userAccount) throws SQLException {
        if (!verifyAccountByUsername(userAccount.getUsername())) {
            database.insertAccount(userAccount);
            this.userAccounts = database.fetchUserAccounts();
        }
    }

    // Edit User Account
    public boolean editUserAccount(UserAccount userAccount) throws SQLException {
        for (UserAccount validAccount : this.userAccounts) {
            if (validAccount.getAccountId() == userAccount.getAccountId()) {
                validAccount.setFirstName(userAccount.getFirstName());
                validAccount.setLastName(userAccount.getLastName());
                validAccount.setUsername(userAccount.getUsername());
                validAccount.setBirthday(userAccount.getBirthday());
                validAccount.setPassword(userAccount.getPassword());
                validAccount.setEmail(userAccount.getEmail());

                database.updateAccount(userAccount);
                System.out.println("Account modified successfully!");
                return true;
            }
        }
        System.out.println("User Account not found");
        return false;
    }

    // Edit User Account Password
    public boolean editUserAccountPassword(int userAccountId, String password) throws SQLException {
        for (UserAccount validAccount : this.userAccounts) {
            if (validAccount.getAccountId() == userAccountId) {
                validAccount.setPassword(password);
                database.updateAccountPassword(userAccountId, password);
                System.out.println("Account password modified successfully!");
                return true;
            }
        }
        System.out.println("User Accousnt not found");
        return false;
    }

    // Delete User Account
    public void deleteUserAccount(UserAccount userAccount) throws SQLException {
        for (UserAccount validUserAccount : this.userAccounts) {
            if (validUserAccount.getAccountId() == userAccount.getAccountId()) {
                userAccounts.remove(validUserAccount);

                database.deleteUserAccount(userAccount);
                System.out.println("Transaction deleted successfully!\n");
                break;
            }
        }
    }

    // Verify User Account
    public boolean verifyUserAccount(int accountId) throws SQLException {
        return database.verifyUserAccount(accountId);
    }

    // Verify User account by username
    public boolean verifyAccountByUsername(String username) throws SQLException {
        return database.verifyAccountByUsername(username);
    }

    // Get User Account
    public UserAccount getUserAccount(int accountId) throws SQLException {
        UserAccount userAccount;

        if (database.verifyUserAccount(accountId)) {
            userAccount = database.getUserAccount(accountId);
            return userAccount;
        }
        return null;
    }

    // Get User Accounts
    public List<UserAccount> getUserAccounts() throws SQLException {
        return this.userAccounts;
    }

    // Fetch all user accounts from the database
    public void fetchUserAccounts() throws SQLException {
        this.userAccounts = database.fetchUserAccounts();
    }
}