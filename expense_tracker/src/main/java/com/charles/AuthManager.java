package com.charles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.mindrot.jbcrypt.BCrypt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class AuthManager {
    private UserAccountManager userAccountManager;
    private List<UserAccount> userAccounts = new ArrayList<>();
    private int failedLoginAttempts = 0;
    private int accountId;
    private String email;
    private static final String TOKEN_FILE = "auth_token.txt";
    private static final String SECRET_KEY = "your_super_secure_and_long_secret_key_123!";
    private static final SecretKey SIGNING_KEY = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
    private final Database database = Database.getInstance();

    public AuthManager(UserAccountManager userAccountManager) throws SQLException {
        this.userAccountManager = userAccountManager;
        this.userAccounts = userAccountManager.getUserAccounts();
    }

    // Login to the system
    public boolean login(String username, String password) throws SQLException, IOException {
        this.userAccounts = userAccountManager.getUserAccounts();
        for (UserAccount userAccount : this.userAccounts) {
            if (userAccount.getUsername().equals(username)) {
                if (BCrypt.checkpw(password, userAccount.getPassword())) {
                    this.getAccountInfo(userAccount.getAccountId(), userAccount.getEmail());
                    return true;
                } else {
                    System.out.println("Password mismatch.");
                    return false;
                }
            }
        }
        System.out.println("Username does not exist. Try again.");
        return false;
    }

    // Fetch all user accounts from the userAccountManager
    public void fetchUserAccounts() throws SQLException {
        this.userAccounts = userAccountManager.getUserAccounts();
    }

    public void getAccountInfo(int accountId, String email) {
        this.accountId = accountId;
        this.email = email;
    }

    public boolean resetPassword(String email) throws SQLException {
        for (UserAccount userAccount : this.userAccounts) {
            if (userAccount.getEmail().equals(email)) {
                System.out.println("Email found.\n");
                return true;
            } else {
                System.out.println("Email not found");
                return false;
            }
        }
        return false;
    }

    public boolean updateAccountPassword(String email, String password) throws SQLException {
        for (UserAccount userAccount : this.userAccounts) {
            if (userAccount.getEmail().equals(email)) {
                System.out.println("Email found.\n");
                userAccountManager.editUserAccountPassword(userAccount.getAccountId(), password);
                return true;
            } else {
                System.out.println("Email not found");
                return false;
            }
        }
        return true;
    }
    
    public String generateSessionToken(int accountId) {
        return Jwts.builder()
                .subject(String.valueOf(accountId))
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24-hour expiration
                .signWith(SIGNING_KEY)
                .compact();
    }

    public void saveSession(String token, String sessionId, int accountId) throws IOException, SQLException {
        Files.write(Paths.get(TOKEN_FILE), token.getBytes());
        database.insertSession(sessionId, accountId);
    }

    public String validateSessionToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SIGNING_KEY) // Use the SecretKey for verification
                    .build() // Build the parser
                    .parseSignedClaims(String.valueOf(token))
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            System.out.println("Invalid or expired token.");
            return null;
        }
    }

    public String loadSessionToken() throws IOException {
        if (Files.exists(Paths.get(TOKEN_FILE))) {
            return new String(Files.readAllBytes(Paths.get(TOKEN_FILE)));
        }
        return null;
    }

    public void terminateSession() throws IOException {
        Files.deleteIfExists(Paths.get(TOKEN_FILE));
    }

    public boolean logout() throws IOException, SQLException {
        terminateSession();
        database.closeConnection();
        return false;
    }

    public UserAccountManager getUserAccountManager() {
        return userAccountManager;
    }

    public void setUserAccountManager(UserAccountManager userAccountManager) {
        this.userAccountManager = userAccountManager;
    }

    public int getFailedLoginAttempts() {
        return this.failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int FailedLoginAttempts) {
        this.failedLoginAttempts = FailedLoginAttempts;
    }
   
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAccountId() {
        return this.accountId;
    }

    public void setAccountId(int accountId){
        this.accountId = accountId;
    }
}