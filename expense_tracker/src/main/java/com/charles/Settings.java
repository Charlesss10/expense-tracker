package com.charles;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Settings {
    private Map<String, String> currencies = new HashMap<>();
    private String preferredCurrency = null;
    private final AuthManager authManager;
    private final UserAccountManager userAccountManager;

    public Settings(AuthManager authManager, UserAccountManager userAccountManager) {
        currencies.put("A", "Euro");
        currencies.put("B", "Dollar");
        this.authManager = authManager;
        this.userAccountManager = userAccountManager;
    }

    public void setAccountCurrency() throws SQLException {
        UserAccount userAccount = userAccountManager.getUserAccount(authManager.getAccountId());
        this.preferredCurrency = userAccount.getCurrency();
    }

    public void setCurrency(String userChoice) {
        for (Map.Entry<String, String> currency : currencies.entrySet()) {
            if (userChoice.equalsIgnoreCase(currency.getKey())) {
                this.preferredCurrency = currency.getValue();
                return;
            }
        }
    }

    public String getPreferredCurrency() {
        return this.preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public Map<String, String> getCurrencies() {
        return this.currencies;
    }

    public void setCurrencies(Map<String, String> currencies) {
        this.currencies = currencies;
    }
}